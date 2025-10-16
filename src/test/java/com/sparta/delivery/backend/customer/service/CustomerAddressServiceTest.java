package com.sparta.delivery.backend.customer.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.customer.dto.ReqCreateCustomerAddressDto;
import com.sparta.delivery.backend.customer.dto.ReqUpdateCustomerAddressDto;
import com.sparta.delivery.backend.customer.dto.ResCustomerAddressDto;
import com.sparta.delivery.backend.customer.dto.ResDefaultAddressDto;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.entity.CustomerAddress;
import com.sparta.delivery.backend.customer.repository.CustomerAddressRepository;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@ExtendWith(MockitoExtension.class)
public class CustomerAddressServiceTest {

	@InjectMocks
	private CustomerAddressService customerAddressService;

	@Mock
	private CustomerAddressRepository customerAddressRepository;

	@Mock
	private DongRepository dongRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private UserDetailsImpl userDetails;

	@Mock
	private User mockUser;

	private Customer customer;
	private User user;
	private Dong dong;
	private Sigungu sigungu;
	private Sido sido;
	private Address address;
	private CustomerAddress customerAddress;

	@BeforeEach
	void setUp() {
		sido = Sido.builder()
			.name("서울특별시")
			.code("11")
			.build();
		ReflectionTestUtils.setField(sido, "id", UUID.randomUUID());

		sigungu = Sigungu.builder()
			.sido(sido)
			.name("종로구")
			.code("110")
			.build();
		ReflectionTestUtils.setField(sigungu, "id", UUID.randomUUID());

		dong = Dong.builder()
			.sigungu(sigungu)
			.name("묘동")
			.code("103")
			.build();
		ReflectionTestUtils.setField(dong, "id", UUID.randomUUID());

		user = User.builder()
			.username("testuser")
			.password("encodedPassword")
			.role(UserRoleEnum.CUSTOMER)
			.build();
		ReflectionTestUtils.setField(user, "id", 1L);

		customer = Customer.builder()
			.user(user)
			.nickname("테스트고객")
			.email("test@example.com")
			.phoneNumber("01012345678")
			.build();
		ReflectionTestUtils.setField(customer, "id", UUID.randomUUID());

		address = Address.builder()
			.dong(dong)
			.fullAddress("서울특별시 종로구 돈화문로 27")
			.build();
		ReflectionTestUtils.setField(address, "id", UUID.randomUUID());

		customerAddress = CustomerAddress.builder()
			.customer(customer)
			.address(address)
			.isDefault(true)
			.nickname("우리집")
			.build();
		ReflectionTestUtils.setField(customerAddress, "id", UUID.randomUUID());
	}

	@Nested
	@DisplayName("배송지 등록 테스트")
	class CreateCustomerAddressTest {

		@Test
		@DisplayName("성공 - 첫 번째 배송지 등록 (기본 배송지)")
		void success_firstAddress() {
			// given
			Long userId = 1L;
			ReqCreateCustomerAddressDto requestDto = new ReqCreateCustomerAddressDto();
			ReflectionTestUtils.setField(requestDto, "regionCode", "1111010300");
			ReflectionTestUtils.setField(requestDto, "fullAddress", "서울특별시 종로구 돈화문로 27");
			ReflectionTestUtils.setField(requestDto, "nickname", "우리집");
			ReflectionTestUtils.setField(requestDto, "isDefault", true);

			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.of(customer));
			when(dongRepository.findByCode("103")).thenReturn(Optional.of(dong));
			when(customerAddressRepository.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer))
				.thenReturn(Optional.empty());

			// when
			customerAddressService.createCustomerAddress(requestDto, userDetails);

			// then
			verify(customerRepository, times(1)).findByUserIdAndDeletedAtIsNull(userId);
			verify(dongRepository, times(1)).findByCode("103");
			verify(customerAddressRepository, times(1))
				.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer);
		}

		@Test
		@DisplayName("성공 - 두 번째 배송지 등록 (기본 배송지 변경)")
		void success_secondAddress_changeDefault() {
			// given
			Long userId = 1L;
			CustomerAddress existingDefaultAddress = CustomerAddress.builder()
				.customer(customer)
				.address(address)
				.isDefault(true)
				.nickname("기존집")
				.build();

			ReqCreateCustomerAddressDto requestDto = new ReqCreateCustomerAddressDto();
			ReflectionTestUtils.setField(requestDto, "regionCode", "1111010300");
			ReflectionTestUtils.setField(requestDto, "fullAddress", "서울특별시 종로구 돈화문로 27");
			ReflectionTestUtils.setField(requestDto, "nickname", "새집");
			ReflectionTestUtils.setField(requestDto, "isDefault", true);

			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.of(customer));
			when(dongRepository.findByCode("103")).thenReturn(Optional.of(dong));
			when(customerAddressRepository.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer))
				.thenReturn(Optional.of(existingDefaultAddress));

			// when
			customerAddressService.createCustomerAddress(requestDto, userDetails);

			// then
			assertFalse(existingDefaultAddress.getIsDefault());
			verify(customerAddressRepository, times(1))
				.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer);
		}

		@Test
		@DisplayName("성공 - 기본 배송지가 아닌 배송지 등록")
		void success_nonDefaultAddress() {
			// given
			Long userId = 1L;
			ReqCreateCustomerAddressDto requestDto = new ReqCreateCustomerAddressDto();
			ReflectionTestUtils.setField(requestDto, "regionCode", "1111010300");
			ReflectionTestUtils.setField(requestDto, "fullAddress", "서울특별시 종로구 돈화문로 27");
			ReflectionTestUtils.setField(requestDto, "nickname", "회사");
			ReflectionTestUtils.setField(requestDto, "isDefault", false);

			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.of(customer));
			when(dongRepository.findByCode("103")).thenReturn(Optional.of(dong));

			// when
			customerAddressService.createCustomerAddress(requestDto, userDetails);

			// then
			verify(customerAddressRepository, never())
				.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer);
		}

		@Nested
		@DisplayName("실패")
		class FailCase {

			@Test
			@DisplayName("존재하지 않는 고객")
			void customerNotFound() {
				// given
				Long userId = 999L;
				ReqCreateCustomerAddressDto requestDto = new ReqCreateCustomerAddressDto();

				when(userDetails.getId()).thenReturn(userId);
				when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
					.thenReturn(Optional.empty());

				// when & then
				IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class,
					() -> customerAddressService.createCustomerAddress(requestDto, userDetails)
				);
			}

			@Test
			@DisplayName("존재하지 않는 지역 코드")
			void dongNotFound() {
				// given
				Long userId = 1L;
				ReqCreateCustomerAddressDto requestDto = new ReqCreateCustomerAddressDto();
				ReflectionTestUtils.setField(requestDto, "regionCode", "0000000000");
				ReflectionTestUtils.setField(requestDto, "fullAddress", "잘못된 주소");
				ReflectionTestUtils.setField(requestDto, "nickname", "테스트");
				ReflectionTestUtils.setField(requestDto, "isDefault", false);

				when(userDetails.getId()).thenReturn(userId);
				when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
					.thenReturn(Optional.of(customer));
				when(dongRepository.findByCode("000")).thenReturn(Optional.empty());

				// when & then
				assertThrows(
					IllegalArgumentException.class,
					() -> customerAddressService.createCustomerAddress(requestDto, userDetails)
				);
			}
		}
	}

	@Nested
	@DisplayName("기본 배송지 설정 테스트")
	class SetDefaultAddressTest {

		@Test
		@DisplayName("성공 - 기본 배송지 변경")
		void success() {
			// given
			Long userId = 1L;
			UUID customerAddressId = UUID.randomUUID();

			CustomerAddress existingDefault = CustomerAddress.builder()
				.customer(customer)
				.address(address)
				.isDefault(true)
				.nickname("기존기본")
				.build();

			CustomerAddress newDefault = CustomerAddress.builder()
				.customer(customer)
				.address(address)
				.isDefault(false)
				.nickname("새기본")
				.build();
			ReflectionTestUtils.setField(newDefault, "id", customerAddressId);

			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.of(customer));
			when(customerAddressRepository.findByIdAndDeletedAtIsNull(customerAddressId))
				.thenReturn(Optional.of(newDefault));
			when(customerAddressRepository.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer))
				.thenReturn(Optional.of(existingDefault));

			// when
			customerAddressService.setDefaultAddress(customerAddressId, userDetails);

			// then
			assertFalse(existingDefault.getIsDefault());
			assertTrue(newDefault.getIsDefault());
			verify(customerAddressRepository, times(1))
				.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer);
		}

		@Nested
		@DisplayName("실패")
		class FailCase {

			@Test
			@DisplayName("존재하지 않는 배송지")
			void addressNotFound() {
				// given
				Long userId = 1L;
				UUID invalidId = UUID.randomUUID();

				when(userDetails.getId()).thenReturn(userId);
				when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
					.thenReturn(Optional.of(customer));
				when(customerAddressRepository.findByIdAndDeletedAtIsNull(invalidId))
					.thenReturn(Optional.empty());

				// when & then
				IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class,
					() -> customerAddressService.setDefaultAddress(invalidId, userDetails)
				);

				assertEquals("배송지를 찾을 수 없습니다.", exception.getMessage());
			}

			@Test
			@DisplayName("권한 없음 - 다른 고객의 배송지")
			void noPermission() {
				// given
				Long userId = 1L;
				UUID customerAddressId = UUID.randomUUID();

				User otherUser = User.builder()
					.username("otheruser")
					.password("password")
					.role(UserRoleEnum.CUSTOMER)
					.build();
				ReflectionTestUtils.setField(otherUser, "id", 2L);

				Customer otherCustomer = Customer.builder()
					.user(otherUser)
					.nickname("다른고객")
					.email("other@example.com")
					.phoneNumber("01087654321")
					.build();
				ReflectionTestUtils.setField(otherCustomer, "id", UUID.randomUUID());

				CustomerAddress otherCustomerAddress = CustomerAddress.builder()
					.customer(otherCustomer)
					.address(address)
					.isDefault(false)
					.nickname("다른사람집")
					.build();
				ReflectionTestUtils.setField(otherCustomerAddress, "id", customerAddressId);

				when(userDetails.getId()).thenReturn(userId);
				when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
					.thenReturn(Optional.of(customer));
				when(customerAddressRepository.findByIdAndDeletedAtIsNull(customerAddressId))
					.thenReturn(Optional.of(otherCustomerAddress));

				// when & then
				AccessDeniedException exception = assertThrows(
					AccessDeniedException.class,
					() -> customerAddressService.setDefaultAddress(customerAddressId, userDetails)
				);

				assertEquals("해당 배송지에 대한 권한이 없습니다.", exception.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("배송지 수정 테스트")
	class UpdateCustomerAddressTest {

		@Test
		@DisplayName("성공 - 전체 수정")
		void success_fullUpdate() {
			// given
			Long userId = 1L;
			UUID customerAddressId = UUID.randomUUID();
			ReflectionTestUtils.setField(customerAddress, "id", customerAddressId);
			ReflectionTestUtils.setField(user, "id", userId);

			ReqUpdateCustomerAddressDto requestDto = new ReqUpdateCustomerAddressDto();
			ReflectionTestUtils.setField(requestDto, "regionCode", "1111010300");
			ReflectionTestUtils.setField(requestDto, "fullAddress", "서울특별시 종로구 돈화문로 27");
			ReflectionTestUtils.setField(requestDto, "nickname", "새별명");
			ReflectionTestUtils.setField(requestDto, "isDefault", true);

			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.of(customer));
			when(customerAddressRepository.findByIdAndDeletedAtIsNull(customerAddressId))
				.thenReturn(Optional.of(customerAddress));
			when(dongRepository.findByCode("103")).thenReturn(Optional.of(dong));
			when(customerAddressRepository.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer))
				.thenReturn(Optional.empty());

			// when
			customerAddressService.updateCustomerAddress(customerAddressId, requestDto, userDetails);

			// then
			verify(dongRepository, times(1)).findByCode("103");
			verify(customerAddressRepository, times(1))
				.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer);
		}

		@Test
		@DisplayName("성공 - 기본 배송지 해제")
		void success_unsetDefault() {
			// given
			Long userId = 1L;
			UUID customerAddressId = UUID.randomUUID();
			ReflectionTestUtils.setField(customerAddress, "id", customerAddressId);
			ReflectionTestUtils.setField(customerAddress, "isDefault", true);
			ReflectionTestUtils.setField(user, "id", userId);

			ReqUpdateCustomerAddressDto requestDto = new ReqUpdateCustomerAddressDto();
			ReflectionTestUtils.setField(requestDto, "regionCode", "1111010300");
			ReflectionTestUtils.setField(requestDto, "fullAddress", "서울특별시 종로구 돈화문로 27");
			ReflectionTestUtils.setField(requestDto, "nickname", "우리집");
			ReflectionTestUtils.setField(requestDto, "isDefault", false);

			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.of(customer));
			when(customerAddressRepository.findByIdAndDeletedAtIsNull(customerAddressId))
				.thenReturn(Optional.of(customerAddress));
			when(dongRepository.findByCode("103")).thenReturn(Optional.of(dong));

			// when
			customerAddressService.updateCustomerAddress(customerAddressId, requestDto, userDetails);

			// then
			verify(dongRepository, times(1)).findByCode("103");
		}

		@Nested
		@DisplayName("실패")
		class FailCase {

			@Test
			@DisplayName("권한 없음")
			void noPermission() {
				// given
				Long userId = 1L;
				UUID customerAddressId = UUID.randomUUID();

				User otherUser = User.builder()
					.username("otheruser")
					.password("password")
					.role(UserRoleEnum.CUSTOMER)
					.build();
				ReflectionTestUtils.setField(otherUser, "id", 2L);

				Customer otherCustomer = Customer.builder()
					.user(otherUser)
					.nickname("다른고객")
					.email("other@example.com")
					.phoneNumber("01087654321")
					.build();
				ReflectionTestUtils.setField(otherCustomer, "id", UUID.randomUUID());

				CustomerAddress otherAddress = CustomerAddress.builder()
					.customer(otherCustomer)
					.address(address)
					.isDefault(false)
					.nickname("다른사람집")
					.build();

				ReqUpdateCustomerAddressDto requestDto = new ReqUpdateCustomerAddressDto();

				when(userDetails.getId()).thenReturn(userId);
				when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
					.thenReturn(Optional.of(customer));
				when(customerAddressRepository.findByIdAndDeletedAtIsNull(customerAddressId))
					.thenReturn(Optional.of(otherAddress));

				// when & then
				AccessDeniedException exception = assertThrows(
					AccessDeniedException.class,
					() -> customerAddressService.updateCustomerAddress(customerAddressId, requestDto, userDetails)
				);

				assertEquals("해당 배송지에 대한 권한이 없습니다.", exception.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("배송지 삭제 테스트")
	class DeleteCustomerAddressTest {

		@Test
		@DisplayName("성공 - 배송지 삭제")
		void success() {
			// given
			Long userId = 1L;
			UUID customerAddressId = UUID.randomUUID();
			ReflectionTestUtils.setField(customerAddress, "id", customerAddressId);
			ReflectionTestUtils.setField(user, "id", userId);

			when(userDetails.getId()).thenReturn(userId);
			when(userDetails.getUser()).thenReturn(user);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.of(customer));
			when(customerAddressRepository.findByIdAndDeletedAtIsNull(customerAddressId))
				.thenReturn(Optional.of(customerAddress));

			// when
			customerAddressService.deleteCustomerAddress(customerAddressId, userDetails);

			// then
			verify(customerAddressRepository, times(1)).findByIdAndDeletedAtIsNull(customerAddressId);
		}

		@Nested
		@DisplayName("실패")
		class FailCase {

			@Test
			@DisplayName("존재하지 않는 배송지")
			void addressNotFound() {
				// given
				Long userId = 1L;
				UUID invalidId = UUID.randomUUID();

				when(userDetails.getId()).thenReturn(userId);
				when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
					.thenReturn(Optional.of(customer));
				when(customerAddressRepository.findByIdAndDeletedAtIsNull(invalidId))
					.thenReturn(Optional.empty());

				// when & then
				IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class,
					() -> customerAddressService.deleteCustomerAddress(invalidId, userDetails)
				);

				assertEquals("배송지를 찾을 수 없습니다.", exception.getMessage());
			}

			@Test
			@DisplayName("권한 없음")
			void noPermission() {
				// given
				Long userId = 1L;
				UUID customerAddressId = UUID.randomUUID();

				User otherUser = User.builder()
					.username("otheruser")
					.password("password")
					.role(UserRoleEnum.CUSTOMER)
					.build();

				Customer otherCustomer = Customer.builder()
					.user(otherUser)
					.nickname("다른고객")
					.email("other@example.com")
					.phoneNumber("01087654321")
					.build();
				ReflectionTestUtils.setField(otherCustomer, "id", UUID.randomUUID());

				CustomerAddress otherAddress = CustomerAddress.builder()
					.customer(otherCustomer)
					.address(address)
					.isDefault(false)
					.nickname("다른사람집")
					.build();

				when(userDetails.getId()).thenReturn(userId);
				when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
					.thenReturn(Optional.of(customer));
				when(customerAddressRepository.findByIdAndDeletedAtIsNull(customerAddressId))
					.thenReturn(Optional.of(otherAddress));

				// when & then
				AccessDeniedException exception = assertThrows(
					AccessDeniedException.class,
					() -> customerAddressService.deleteCustomerAddress(customerAddressId, userDetails)
				);

				assertEquals("해당 배송지에 대한 권한이 없습니다.", exception.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("내 배송지 목록 조회 테스트")
	class GetMyCustomerAddressesTest {

		@Test
		@DisplayName("성공 - 배송지 목록 조회")
		void success() {
			// given
			Long userId = 1L;
			List<CustomerAddress> addressList = new ArrayList<>();
			addressList.add(customerAddress);

			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.of(customer));
			when(customerAddressRepository.findAllByCustomerAndDeletedAtIsNullOrderByIsDefaultDescCreatedAtDesc(
				customer))
				.thenReturn(addressList);

			// when
			List<ResCustomerAddressDto> result = customerAddressService.getMyCustomerAddresses(userDetails);

			// then
			assertNotNull(result);
			assertEquals(1, result.size());
			assertThat(result)
				.hasSize(1)
				.first()
				.satisfies(address -> {
					assertThat(address.getNickname()).isEqualTo(customerAddress.getNickname());
					assertThat(address.getFullAddress()).isEqualTo(customerAddress.getFullAddress());
					assertThat(address.getRegionCode()).isEqualTo(customerAddress.getDongCode());
					assertThat(address.getIsDefault()).isTrue();
				});
		}

		@Test
		@DisplayName("성공 - 배송지 없음")
		void success_emptyList() {
			// given
			Long userId = 1L;
			List<CustomerAddress> emptyList = new ArrayList<>();

			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.of(customer));
			when(customerAddressRepository.findAllByCustomerAndDeletedAtIsNullOrderByIsDefaultDescCreatedAtDesc(
				customer))
				.thenReturn(emptyList);

			// when
			List<ResCustomerAddressDto> result = customerAddressService.getMyCustomerAddresses(userDetails);

			// then
			assertNotNull(result);
			assertEquals(0, result.size());
		}
	}

	@Nested
	@DisplayName("기본 배송지 조회 테스트")
	class GetDefaultCustomerAddressTest {

		@Test
		@DisplayName("성공 - 기본 배송지 있음")
		void success_hasDefault() {
			// given
			Long userId = 1L;

			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.of(customer));
			when(customerAddressRepository.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer))
				.thenReturn(Optional.of(customerAddress));

			// when
			ResDefaultAddressDto result = customerAddressService.getDefaultCustomerAddress(userDetails);

			// then
			assertNotNull(result);
			assertTrue(result.isHasDefaultAddress());
			assertNotNull(result.getAddress());
			verify(customerAddressRepository, times(1))
				.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer);
		}

		@Test
		@DisplayName("성공 - 기본 배송지 없음")
		void success_noDefault() {
			// given
			Long userId = 1L;

			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.of(customer));
			when(customerAddressRepository.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer))
				.thenReturn(Optional.empty());

			// when
			ResDefaultAddressDto result = customerAddressService.getDefaultCustomerAddress(userDetails);

			// then
			assertNotNull(result);
			assertFalse(result.isHasDefaultAddress());
			assertNull(result.getAddress());
		}
	}
}
