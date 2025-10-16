// package com.sparta.delivery.backend.address.service;
//
// import java.util.List;
// import java.util.UUID;
//
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.sparta.delivery.backend.address.entity.Address;
// import com.sparta.delivery.backend.address.repository.AddressRepository;
// import com.sparta.delivery.backend.customer.dto.ReqUpdateAddressDto;
// import com.sparta.delivery.backend.region.entity.Dong;
// import com.sparta.delivery.backend.region.repository.DongRepository;
// import com.sparta.delivery.backend.security.UserDetailsImpl;
//
// import lombok.RequiredArgsConstructor;
//
// @Service
// @Transactional
// @RequiredArgsConstructor
// public class AddressService {
//
// 	private final AddressRepository addressRepository;
// 	private final DongRepository dongRepository;
//
// 	/**
// 	 * 주소 등록
// 	 * @param requestDto 주소 등록 요청 DTO
// 	 * @param userDetails 인증된 사용자 정보
// 	 */
// 	public void registerAddress(ReqRegisterAddressDto requestDto, UserDetailsImpl userDetails) {
// 		unsetDefaultAddressIfExists(userDetails);
//
// 		Dong dong = getDongByCode(requestDto.getRegionCode());
//
// 		Address address = Address.builder()
// 			.dong(dong)
// 			.address(requestDto.getAddress())
// 			.build();
// 		addressRepository.save(address);
// 	}
//
// 	/**
// 	 * 기본 주소지 설정
// 	 * @param id 주소 ID
// 	 * @param userDetails 인증된 사용자 정보
// 	 * @return 설정된 기본 주소지 DTO
// 	 */
// 	public ResAddressDto setDefaultAddress(UUID id, UserDetailsImpl userDetails) {
// 		Address address = getAddressById(id);
//
// 		unsetDefaultAddressIfExists(userDetails);
// 		address.setDefault();
// 		return ResAddressDto.from(address);
// 	}
//
// 	private void unsetDefaultAddressIfExists(UserDetailsImpl userDetails) {
// 		// 기본 주소지가 이미 존재하면 기본 주소지 해제
// 		addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(userDetails.getId())
// 			.ifPresent(Address::unsetDefault);
// 	}
//
// 	/**
// 	 * 내 주소지 목록 조회
// 	 * @param userDetails 인증된 사용자 정보
// 	 * @return 내 주소지 목록 DTO 리스트
// 	 */
// 	@Transactional(readOnly = true)
// 	public List<ResAddressDto> getMyAddresses(UserDetailsImpl userDetails) {
// 		List<Address> findAddresses = addressRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(
// 			userDetails.getId());
// 		return findAddresses.stream().map(ResAddressDto::from).toList();
// 	}
//
// 	/**
// 	 * 기본 주소지 조회
// 	 * @param userDetails 인증된 사용자 정보
// 	 * @return 기본 주소지 DTO
// 	 */
// 	@Transactional(readOnly = true)
// 	public ResAddressDto getDefaultAddress(UserDetailsImpl userDetails) {
// 		Address defaultAddress = addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(userDetails.getId())
// 			.orElseThrow(() -> new NotFoundException("요청한 리소스를 찾을 수 없습니다."));
// 		return ResAddressDto.from(defaultAddress);
// 	}
//
// 	/**
// 	 * 주소 수정
// 	 * @param id 주소 ID
// 	 * @param requestDto 주소 수정 요청 DTO
// 	 * @return 수정된 주소 DTO
// 	 */
// 	public ResAddressDto updateAddress(UUID id, ReqUpdateAddressDto requestDto) {
// 		Address address = getAddressById(id);
// 		Dong dong = getDongByCode(requestDto.getRegionCode());
//
// 		address.update(dong, requestDto.getAddress());
// 		return ResAddressDto.from(address);
// 	}
//
// 	/**
// 	 * 주소 삭제 (소프트 삭제)
// 	 * @param id 주소 ID
// 	 * @param userDetails 인증된 사용자 정보
// 	 */
// 	public void deleteAddress(UUID id, UserDetailsImpl userDetails) {
// 		Address address = getAddressById(id);
// 		address.softDelete(userDetails.getUser().getId());
// 	}
//
// 	private Dong getDongByCode(String code) {
// 		return dongRepository.findByCode(code).orElseThrow(
// 			() -> new NotFoundException("해당 주소지를 찾을 수 없습니다.")
// 		);
// 	}
//
// 	private Address getAddressById(UUID id) {
// 		return addressRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
// 			() -> new NotFoundException("요청한 리소스를 찾을 수 없습니다.")
// 		);
// 	}
// }
