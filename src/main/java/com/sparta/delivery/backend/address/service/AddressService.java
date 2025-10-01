package com.sparta.delivery.backend.address.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.address.dto.ReqRegisterAddressDto;
import com.sparta.delivery.backend.address.dto.ReqUpdateAddressDto;
import com.sparta.delivery.backend.address.dto.ResAddressDto;
import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.address.repository.AddressRepository;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.global.excpetion.NotFoundException;
import com.sparta.delivery.backend.global.excpetion.UnauthorizedException;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

	private final AddressRepository addressRepository;
	private final DongRepository dongRepository;
	private final CustomerRepository customerRepository;

	@Transactional
	public void registerAddress(ReqRegisterAddressDto requestDto, UserDetailsImpl user) {
		Dong dong = dongRepository.findByCode(requestDto.getRegionCode()).orElseThrow(
			() -> new NotFoundException("주소지를 찾을 수 없습니다.")
		);

		Customer customer = customerRepository.findByUserId(user.getId()).orElseThrow(
			() -> new NotFoundException("해당 User를 찾을 수 없습니다.")
		);

		Address address = Address.builder()
			.dong(dong)
			.address(requestDto.getAddress())
			.customer(customer)
			.build();
		addressRepository.save(address);
	}

	@Transactional(readOnly = true)
	public List<ResAddressDto> getMyAddresses(UserDetailsImpl user) {
		List<Address> findAddresses = addressRepository.findAllByUserId(user.getId());
		return findAddresses.stream().map(ResAddressDto::from).toList();
	}

	@Transactional
	public ResAddressDto updateAddress(UUID id, ReqUpdateAddressDto requestDto, UserDetailsImpl user) {
		Address address = addressRepository.findById(id).orElseThrow(
			() -> new NotFoundException("요청한 리소스를 찾을 수 없습니다.")
		);

		if (!address.getCustomer().getUser().getId().equals(user.getId())) {
			throw new UnauthorizedException("권한이 없습니다.");
		}

		Dong dong = dongRepository.findByCode(requestDto.getRegionCode()).orElseThrow(
			() -> new NotFoundException("해당 주소지를 찾을 수 없습니다.")
		);

		address.update(dong, requestDto.getAddress());
		return ResAddressDto.from(address);
	}
}
