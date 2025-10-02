package com.sparta.delivery.backend.address.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sparta.delivery.backend.address.dto.ReqRegisterAddressDto;
import com.sparta.delivery.backend.address.dto.ReqUpdateAddressDto;
import com.sparta.delivery.backend.address.dto.ResAddressDto;
import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.address.repository.AddressRepository;
import com.sparta.delivery.backend.global.excpetion.NotFoundException;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

	@Mock
	private AddressRepository addressRepository;

	@Mock
	private DongRepository dongRepository;

	@InjectMocks
	private AddressService addressService;

	private User user;
	private UserDetailsImpl userDetails;
	private Dong dong;
	private Address address;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.username("test")
			.password("password")
			.role(UserRoleEnum.CUSTOMER)
			.build();

		userDetails = new UserDetailsImpl(user);

		dong = Dong.builder()
			.code("123")
			.name("망원1동")
			.build();

		address = Address.builder()
			.dong(dong)
			.address("서울특별시 마포구 망원1동 마포나루길 467")
			.user(user)
			.build();
	}

	@Nested
	@DisplayName("주소 등록 테스트")
	class RegisterAddressTest {

		@Test
		@DisplayName("성공")
		void registerAddress_Success() {
			ReqRegisterAddressDto requestDto = new ReqRegisterAddressDto(
				"123",
				"서울특별시 마포구 망원1동 마포나루길 467");

			given(dongRepository.findByCode(requestDto.getRegionCode()))
				.willReturn(Optional.of(dong));

			given(addressRepository.save(any(Address.class)))
				.willReturn(address);

			addressService.registerAddress(requestDto, userDetails);

			then(dongRepository).should(times(1)).findByCode(requestDto.getRegionCode());
			then(addressRepository).should(times(1)).save(any(Address.class));
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 지역 코드")
		void registerAddress_Fail_DongNotFound() {
			ReqRegisterAddressDto requestDto = new ReqRegisterAddressDto(
				"999",
				"서울특별시 마포구 망원1동 마포나루길 467");

			given(dongRepository.findByCode(requestDto.getRegionCode()))
				.willReturn(Optional.empty());

			assertThatThrownBy(() -> addressService.registerAddress(requestDto, userDetails))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("주소지를 찾을 수 없습니다.");

			then(addressRepository).should(never()).save(any(Address.class));
		}
	}

	@Nested
	@DisplayName("내 주소 목록 조회 테스트")
	class GetMyAddressesTest {

		@Test
		@DisplayName("성공")
		void getMyAddresses_Success() {
			Address anotherAddress = Address.builder()
				.dong(dong)
				.address("강남대로 456")
				.user(user)
				.build();

			List<Address> addresses = List.of(address, anotherAddress);

			given(addressRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(user.getId()))
				.willReturn(addresses);

			List<ResAddressDto> result = addressService.getMyAddresses(userDetails);

			assertThat(result).hasSize(2);
			then(addressRepository).should(times(1))
				.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(user.getId());
		}
	}

	@Nested
	@DisplayName("주소 수정 테스트")
	class UpdateAddressTest {

		@Test
		@DisplayName("성공")
		void updateAddress_Success() {
			UUID addressId = address.getId();
			ReqUpdateAddressDto requestDto = new ReqUpdateAddressDto(
				"136",
				"새로운 주소 789"
			);

			given(addressRepository.findById(addressId))
				.willReturn(Optional.of(address));

			given(dongRepository.findByCode(requestDto.getRegionCode()))
				.willReturn(Optional.of(dong));

			ResAddressDto result = addressService.updateAddress(addressId, requestDto, userDetails);

			assertThat(result).isNotNull();
			then(addressRepository).should(times(1)).findById(addressId);
			then(dongRepository).should(times(1)).findByCode(requestDto.getRegionCode());
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 주소 ID")
		void updateAddress_Fail_AddressNotFound() {
			UUID addressId = UUID.randomUUID();
			ReqUpdateAddressDto requestDto = new ReqUpdateAddressDto(
				"123",
				"새로운 주소 789"
			);

			given(addressRepository.findById(addressId))
				.willReturn(Optional.empty());

			assertThatThrownBy(() -> addressService.updateAddress(addressId, requestDto, userDetails))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("요청한 리소스를 찾을 수 없습니다.");

			then(dongRepository).should(never()).findByCode(anyString());
		}

		@Test
		@DisplayName("주소 수정: 실패 - 존재하지 않는 지역 코드")
		void updateAddress_Fail_RegionDongNotFound() {
			UUID addressId = address.getId();
			ReqUpdateAddressDto requestDto = new ReqUpdateAddressDto(
				"999",
				"새로운 주소 789"
			);

			given(addressRepository.findById(addressId))
				.willReturn(Optional.of(address));

			given(dongRepository.findByCode(requestDto.getRegionCode()))
				.willReturn(Optional.empty());

			assertThatThrownBy(() -> addressService.updateAddress(addressId, requestDto, userDetails))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("해당 주소지를 찾을 수 없습니다.");
		}
	}

	@Nested
	@DisplayName("주소 삭제 테스트")
	class DeleteAddressTest {

		@Test
		@DisplayName("주소 삭제: 성공")
		void deleteAddress_Success() {
			UUID addressId = address.getId();

			given(addressRepository.findByIdAndDeletedAtIsNull(addressId))
				.willReturn(Optional.of(address));

			addressService.deleteAddress(addressId, userDetails);

			then(addressRepository).should(times(1)).findByIdAndDeletedAtIsNull(addressId);
		}

		@Test
		@DisplayName("주소 삭제: 실패 - 존재하지 않는 주소 ID")
		void deleteAddress_Fail_AddressNotFound() {
			UUID addressId = UUID.randomUUID();

			given(addressRepository.findByIdAndDeletedAtIsNull(addressId))
				.willReturn(Optional.empty());

			assertThatThrownBy(() -> addressService.deleteAddress(addressId, userDetails))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("요청한 리소스를 찾을 수 없습니다.");
		}

		@Test
		@DisplayName("주소 삭제: 실패 - 이미 삭제된 주소")
		void deleteAddress_Fail_AlreadyDeleted() {
			UUID addressId = address.getId();

			given(addressRepository.findByIdAndDeletedAtIsNull(addressId))
				.willReturn(Optional.empty());

			assertThatThrownBy(() -> addressService.deleteAddress(addressId, userDetails))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("요청한 리소스를 찾을 수 없습니다.");
		}
	}
}