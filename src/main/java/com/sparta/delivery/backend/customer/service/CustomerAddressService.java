package com.sparta.delivery.backend.customer.service;

import java.util.List;
import java.util.UUID;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.address.repository.AddressRepository;
import com.sparta.delivery.backend.customer.dto.ReqCreateCustomerAddressDto;
import com.sparta.delivery.backend.customer.dto.ReqUpdateCustomerAddressDto;
import com.sparta.delivery.backend.customer.dto.ResCustomerAddressDto;
import com.sparta.delivery.backend.customer.dto.ResDefaultAddressDto;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.entity.CustomerAddress;
import com.sparta.delivery.backend.customer.repository.CustomerAddressRepository;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerAddressService {

	private final CustomerAddressRepository customerAddressRepository;
	private final AddressRepository addressRepository;
	private final DongRepository dongRepository;
	private final CustomerRepository customerRepository;

	@Transactional
	public void createCustomerAddress(ReqCreateCustomerAddressDto requestDto,
		UserDetailsImpl userDetails) {
		Customer customer = getCustomer(userDetails);

		if (requestDto.getIsDefault()) {
			unsetDefaultAddress(customer);
		}

		Dong dong = getDongByCode(requestDto.getRegionCode());

		Address address = Address.builder()
			.dong(dong)
			.fullAddress(requestDto.getFullAddress())
			.location(createPoint(requestDto.getLongitude(), requestDto.getLatitude()))
			.build();

		CustomerAddress customerAddress = CustomerAddress.builder()
			.customer(customer)
			.address(address)
			.isDefault(requestDto.getIsDefault())
			.nickname(requestDto.getNickname())
			.build();

		customer.addCustomerAddress(customerAddress);
	}

	@Transactional
	public void setDefaultAddress(UUID customerAddressId, UserDetailsImpl userDetails) {
		Customer customer = getCustomer(userDetails);
		CustomerAddress customerAddress = getCustomerAddress(customerAddressId);

		validateOwnership(customerAddress, customer);

		unsetDefaultAddress(customer);

		customerAddress.setDefault();
	}

	@Transactional
	public void updateCustomerAddress(UUID customerAddressId, ReqUpdateCustomerAddressDto requestDto,
		UserDetailsImpl userDetails) {
		Customer customer = getCustomer(userDetails);
		CustomerAddress customerAddress = getCustomerAddress(customerAddressId);

		validateOwnership(customerAddress, customer);

		Dong dong = getDongByCode(requestDto.getRegionCode());

		Address newAddress = Address.builder()
			.dong(dong)
			.fullAddress(requestDto.getFullAddress())
			.location(createPoint(requestDto.getLongitude(), requestDto.getLatitude()))
			.build();

		if (requestDto.getIsDefault()) {
			unsetDefaultAddress(customer);
		}

		customerAddress.updateAddress(userDetails.getId(),
			newAddress,
			requestDto.getIsDefault(),
			requestDto.getNickname());
	}

	@Transactional
	public void deleteCustomerAddress(UUID customerAddressId, UserDetailsImpl userDetails) {
		Customer customer = getCustomer(userDetails);
		CustomerAddress customerAddress = getCustomerAddress(customerAddressId);

		validateOwnership(customerAddress, customer);

		customerAddress.delete(userDetails.getUser().getId());
	}

	public List<ResCustomerAddressDto> getMyCustomerAddresses(UserDetailsImpl userDetails) {
		Customer customer = getCustomer(userDetails);

		List<CustomerAddress> customerAddresses = customerAddressRepository
			.findAllByCustomerAndDeletedAtIsNullOrderByIsDefaultDescCreatedAtDesc(customer);

		return customerAddresses.stream()
			.map(ResCustomerAddressDto::from)
			.toList();
	}

	public ResDefaultAddressDto getDefaultCustomerAddress(UserDetailsImpl userDetails) {
		Customer customer = getCustomer(userDetails);

		return customerAddressRepository
			.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer)
			.map(ResCustomerAddressDto::from)
			.map(ResDefaultAddressDto::of)
			.orElse(ResDefaultAddressDto.empty());
	}

	private void unsetDefaultAddress(Customer customer) {
		customerAddressRepository
			.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer)
			.ifPresent(CustomerAddress::unsetDefault);
	}

	private void validateOwnership(CustomerAddress customerAddress, Customer customer) {
		if (!customerAddress.getCustomer().getId().equals(customer.getId())) {
			throw new AccessDeniedException("해당 배송지에 대한 권한이 없습니다.");
		}
	}

	private Customer getCustomer(UserDetailsImpl userDetails) {
		return customerRepository.findByUserIdAndDeletedAtIsNull(userDetails.getId())
			.orElseThrow(() -> new IllegalArgumentException("고객 정보를 찾을 수 없습니다."));
	}

	private CustomerAddress getCustomerAddress(UUID id) {
		return customerAddressRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new IllegalArgumentException("배송지를 찾을 수 없습니다."));
	}

	private Dong getDongByCode(String code) {
		if (code == null || code.length() != 10) {
			throw new IllegalArgumentException("법정동 코드는 10자리여야 합니다.");
		}
		String dongCode = code.substring(5, 8);
		return dongRepository.findByCode(dongCode)
			.orElseThrow(() -> new IllegalArgumentException("해당 지역을 찾을 수 없습니다."));
	}

	// 경도, 위도 좌표로 Point 객체 생성 (SRID 4326 사용)
	private Point createPoint(Double longitude, Double latitude) {
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
		return geometryFactory.createPoint(new Coordinate(longitude, latitude));
	}
}
