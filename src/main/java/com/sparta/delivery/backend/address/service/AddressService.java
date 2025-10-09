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

		// 기본 주소지가 이미 존재하면 기본 주소지 해제
		unsetDefaultAddressIfExists(userDetails);

		Address address = Address.builder()
			.dong(dong)
			.address(requestDto.getAddress())
			.user(userDetails.getUser())
			.build();
		addressRepository.save(address);
	}

	@Transactional
	public ResAddressDto setDefaultAddress(UUID id, UserDetailsImpl userDetails) {
		Address address = addressRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
			() -> new NotFoundException("요청한 리소스를 찾을 수 없습니다.")
		);

		unsetDefaultAddressIfExists(userDetails);
		address.setDefault();
		return ResAddressDto.from(address);
	}

	private void unsetDefaultAddressIfExists(UserDetailsImpl userDetails) {
		addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(userDetails.getId())
			.ifPresent(Address::unsetDefault);
	}

	@Transactional(readOnly = true)
	public List<ResAddressDto> getMyAddresses(UserDetailsImpl userDetails) {
		List<Address> findAddresses = addressRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(
			userDetails.getId());
		return findAddresses.stream().map(ResAddressDto::from).toList();
	}

	@Transactional(readOnly = true)
	public ResAddressDto getDefaultAddress(UserDetailsImpl userDetails) {
		Address defaultAddress = addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(userDetails.getId())
			.orElseThrow(() -> new NotFoundException("요청한 리소스를 찾을 수 없습니다."));
		return ResAddressDto.from(defaultAddress);
	}

	@Transactional
	public ResAddressDto updateAddress(UUID id, ReqUpdateAddressDto requestDto) {
		Address address = addressRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
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
