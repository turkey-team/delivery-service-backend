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
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
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
	private Address otherAddress;

	@BeforeEach
	void setUp() {
		user = createUser("testUser", "password123");
		userDetails = new UserDetailsImpl(user);

		Sido seoul = createSido("서울특별시", "11");
		Sigungu seocho = createSigungu(seoul, "서초구", "650");
		dong = createDong(seocho, "136", "서초동");
		address = createAddress(userDetails.getUser(), dong, "서울특별시 서초구 서초동 강남대로 123");
		otherAddress = createAddress(userDetails.getUser(), dong, "서울특별시 서초구 서초동 강남대로 456");
	}

	@Nested
	@DisplayName("주소 등록 테스트")
	class RegisterAddressTest {

		@Test
		@DisplayName("성공 - 등록 완료")
		void registerAddress_Success() {
			ReqRegisterAddressDto requestDto = new ReqRegisterAddressDto(
				"136",
				"서울특별시 서초구 서초동 강남대로 123");

			given(dongRepository.findByCode(requestDto.getRegionCode()))
				.willReturn(Optional.of(dong));

			given(addressRepository.save(any(Address.class)))
				.willReturn(address);

			addressService.registerAddress(requestDto, userDetails);

			then(addressRepository).should(times(1))
				.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(userDetails.getId());
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
				.hasMessage("해당 주소지를 찾을 수 없습니다.");

			then(addressRepository).should(never()).save(any(Address.class));
		}
	}

	@Nested
	@DisplayName("주소 조회 테스트")
	class GetMyAddressesTest {

		@Test
		@DisplayName("성공 - 목록 조회 완료")
		void getMyAddresses_Success() {
			Address anotherAddress = createAddress(user, dong, "강남대로 456");

			List<Address> addresses = List.of(address, anotherAddress);

			given(addressRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(user.getId()))
				.willReturn(addresses);

			List<ResAddressDto> result = addressService.getMyAddresses(userDetails);

			assertThat(result).hasSize(2);
			then(addressRepository).should(times(1))
				.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(user.getId());
		}

		@Test
		@DisplayName("성공 - 기본 주소지 조회")
		void getDefaultAddress_Success() {
			given(addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(user.getId()))
				.willReturn(Optional.of(address));

			ResAddressDto result = addressService.getDefaultAddress(userDetails);

			assertThat(result).isNotNull();
			assertThat(result.isDefault()).isTrue();
			assertThat(result.getAddress()).isEqualTo(address.getAddress());
			then(addressRepository).should(times(1))
				.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(user.getId());
		}
	}

	@Nested
	@DisplayName("주소 수정 테스트")
	class UpdateAddressTest {

		@Test
		@DisplayName("성공 - 수정 완료")
		void updateAddress_Success() {
			UUID addressId = address.getId();
			ReqUpdateAddressDto requestDto = new ReqUpdateAddressDto(
				"136",
				"새로운 주소 789"
			);

			given(addressRepository.findByIdAndDeletedAtIsNull(addressId))
				.willReturn(Optional.of(address));

			given(dongRepository.findByCode(requestDto.getRegionCode()))
				.willReturn(Optional.of(dong));

			ResAddressDto result = addressService.updateAddress(addressId, requestDto);

			assertThat(result).isNotNull();
			assertThat(result.getAddress()).isEqualTo("새로운 주소 789");
			then(addressRepository).should(times(1)).findByIdAndDeletedAtIsNull(addressId);
			then(dongRepository).should(times(1)).findByCode(requestDto.getRegionCode());
		}

		@Test
		@DisplayName("성공 - 기본 주소지 수정")
		void updateAddress_Success_ChangeDefault() {
			UUID addressId = otherAddress.getId();

			given(addressRepository.findByIdAndDeletedAtIsNull(addressId))
				.willReturn(Optional.of(otherAddress));

			given(addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(user.getId()))
				.willReturn(Optional.of(address));

			ResAddressDto result = addressService.setDefaultAddress(addressId, userDetails);

			assertThat(result).isNotNull();
			assertThat(result.isDefault()).isTrue();
			assertThat(result.getAddress()).isEqualTo(otherAddress.getAddress());
			assertThat(address.getIsDefault()).isFalse();
			then(addressRepository).should(times(1)).findByIdAndDeletedAtIsNull(addressId);
			then(addressRepository).should(times(1))
				.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(userDetails.getId());
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 주소 ID")
		void updateAddress_Fail_AddressNotFound() {
			UUID addressId = UUID.randomUUID();
			ReqUpdateAddressDto requestDto = new ReqUpdateAddressDto(
				"123",
				"새로운 주소 789"
			);

			given(addressRepository.findByIdAndDeletedAtIsNull(addressId))
				.willReturn(Optional.empty());

			assertThatThrownBy(() -> addressService.updateAddress(addressId, requestDto))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("요청한 리소스를 찾을 수 없습니다.");

			then(dongRepository).should(never()).findByCode(anyString());
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 지역 코드")
		void updateAddress_Fail_RegionDongNotFound() {
			UUID addressId = address.getId();
			ReqUpdateAddressDto requestDto = new ReqUpdateAddressDto(
				"999",
				"새로운 주소 789"
			);

			given(addressRepository.findByIdAndDeletedAtIsNull(addressId))
				.willReturn(Optional.of(address));

			given(dongRepository.findByCode(requestDto.getRegionCode()))
				.willReturn(Optional.empty());

			assertThatThrownBy(() -> addressService.updateAddress(addressId, requestDto))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("해당 주소지를 찾을 수 없습니다.");
		}
	}

	@Nested
	@DisplayName("주소 삭제 테스트")
	class DeleteAddressTest {

		@Test
		@DisplayName("성공 - 삭제 완료")
		void deleteAddress_Success() {
			UUID addressId = address.getId();

			given(addressRepository.findByIdAndDeletedAtIsNull(addressId))
				.willReturn(Optional.of(address));

			addressService.deleteAddress(addressId, userDetails);

			then(addressRepository).should(times(1)).findByIdAndDeletedAtIsNull(addressId);
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 주소 ID")
		void deleteAddress_Fail_AddressNotFound() {
			UUID addressId = UUID.randomUUID();

			given(addressRepository.findByIdAndDeletedAtIsNull(addressId))
				.willReturn(Optional.empty());

			assertThatThrownBy(() -> addressService.deleteAddress(addressId, userDetails))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("요청한 리소스를 찾을 수 없습니다.");
		}

		@Test
		@DisplayName("실패 - 이미 삭제된 주소")
		void deleteAddress_Fail_AlreadyDeleted() {
			UUID addressId = address.getId();

			given(addressRepository.findByIdAndDeletedAtIsNull(addressId))
				.willReturn(Optional.empty());

			assertThatThrownBy(() -> addressService.deleteAddress(addressId, userDetails))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("요청한 리소스를 찾을 수 없습니다.");
		}
	}

	private User createUser(String username, String password) {
		return User.builder()
			.username(username)
			.password(password)
			.role(UserRoleEnum.CUSTOMER)
			.build();
	}

	private Address createAddress(User user, Dong dong, String address) {
		return Address.builder()
			.user(user)
			.dong(dong)
			.address(address)
			.build();
	}

	private Sido createSido(String name, String code) {
		return Sido.builder()
			.name(name)
			.code(code)
			.build();
	}

	private Sigungu createSigungu(Sido sido, String name, String code) {
		return Sigungu.builder()
			.sido(sido)
			.name(name)
			.code(code)
			.build();
	}

	private Dong createDong(Sigungu sigungu, String code, String name) {
		return Dong.builder()
			.sigungu(sigungu)
			.code(code)
			.name(name)
			.build();
	}
}