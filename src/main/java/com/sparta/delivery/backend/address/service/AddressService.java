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
import com.sparta.delivery.backend.global.excpetion.NotFoundException;
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

	@Transactional
	public void registerAddress(ReqRegisterAddressDto requestDto, UserDetailsImpl userDetails) {
		Dong dong = dongRepository.findByCode(requestDto.getRegionCode()).orElseThrow(
			() -> new NotFoundException("주소지를 찾을 수 없습니다.")
		);

		Address address = Address.builder()
			.dong(dong)
			.address(requestDto.getAddress())
			.user(userDetails.getUser())
			.build();
		addressRepository.save(address);
	}

	@Transactional(readOnly = true)
	public List<ResAddressDto> getMyAddresses(UserDetailsImpl user) {
		List<Address> findAddresses = addressRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(
			user.getId());
		return findAddresses.stream().map(ResAddressDto::from).toList();
	}

	@Transactional
	public ResAddressDto updateAddress(UUID id, ReqUpdateAddressDto requestDto, UserDetailsImpl user) {
		Address address = addressRepository.findById(id).orElseThrow(
			() -> new NotFoundException("요청한 리소스를 찾을 수 없습니다.")
		);

		Dong dong = dongRepository.findByCode(requestDto.getRegionCode()).orElseThrow(
			() -> new NotFoundException("해당 주소지를 찾을 수 없습니다.")
		);

		address.update(dong, requestDto.getAddress());
		return ResAddressDto.from(address);
	}

	@Transactional
	public void deleteAddress(UUID id, UserDetailsImpl userDetails) {
		Address address = addressRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
			() -> new NotFoundException("요청한 리소스를 찾을 수 없습니다.")
		);

		address.softDelete(userDetails.getUser().getId());
	}
}
