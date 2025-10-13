package com.sparta.delivery.backend.customer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import com.sparta.delivery.backend.customer.dto.ResGetCustomerDto;
import com.sparta.delivery.backend.customer.dto.ResGetMyCustomerDto;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
	@InjectMocks
	private CustomerService customerService;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private UserDetailsImpl userDetails;

	private Customer customer;
	private User user;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.username("testuser")
			.password("encodedPassword")
			.role(UserRoleEnum.CUSTOMER)
			.build();

		customer = Customer.builder()
			.user(user)
			.nickname("테스트닉네임")
			.email("test@example.com")
			.phoneNumber("010-1234-5678")
			.build();
	}

	@Nested
	@DisplayName("고객 마이페이지 조회 테스트")
	class GetCustomerByIdTest {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			Long userId = 1L;
			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(customer));

			// when
			ResGetMyCustomerDto result = customerService.getCurrentCustomer(userDetails);

			// then
			assertNotNull(result);
			assertEquals(customer.getNickname(), result.getNickname());
			assertEquals(customer.getEmail(), result.getEmail());
			assertEquals(customer.getPhoneNumber(), result.getPhoneNumber());
		}

		@Nested
		@DisplayName("실패")
		class FailCase {

			@Test
			@DisplayName("존재하지 않는 유저")
			void userNotFound() {
				// given
				Long userId = 999L;
				when(userDetails.getId()).thenReturn(userId);
				when(customerRepository.findByUserIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

				// when & then
				IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class,
					() -> customerService.getCurrentCustomer(userDetails)
				);

				assertEquals("잘못된 유저 아이디 입니다.", exception.getMessage());
			}

			@Test
			@DisplayName("삭제된 유저")
			void deletedUser() {
				// given
				Long userId = 1L;

				when(userDetails.getId()).thenReturn(userId);
				when(customerRepository.findByUserIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

				// when & then
				IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class,
					() -> customerService.getCurrentCustomer(userDetails)
				);

				assertEquals("잘못된 유저 아이디 입니다.", exception.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("고객 정보 조회 테스트 (관리자용)")
	class GetCustomerByUserPublicIdTest {

		@Test
		@DisplayName("성공 - UUID로 고객 조회")
		void success() {
			// given
			UUID userPublicId = UUID.randomUUID();
			when(customerRepository.findByUserPublicIdAndDeletedAtNull(userPublicId))
				.thenReturn(Optional.of(customer));

			// when
			ResGetCustomerDto result = customerService.getCustomerByUserPublicId(userPublicId);

			// then
			assertNotNull(result);
			assertEquals(customer.getNickname(), result.getNickname());
			assertEquals(customer.getEmail(), result.getEmail());
			assertEquals(customer.getPhoneNumber(), result.getPhoneNumber());
			verify(customerRepository, times(1)).findByUserPublicIdAndDeletedAtNull(userPublicId);
		}

		@Nested
		@DisplayName("실패")
		class FailCase {

			@Test
			@DisplayName("존재하지 않는 유저 공개 ID")
			void userPublicIdNotFound() {
				// given
				UUID invalidPublicId = UUID.randomUUID();
				when(customerRepository.findByUserPublicIdAndDeletedAtNull(invalidPublicId))
					.thenReturn(Optional.empty());

				// when & then
				IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class,
					() -> customerService.getCustomerByUserPublicId(invalidPublicId)
				);

				assertEquals("잘못된 유저 아이디 입니다.", exception.getMessage());
				verify(customerRepository, times(1)).findByUserPublicIdAndDeletedAtNull(invalidPublicId);
			}

			@Test
			@DisplayName("삭제된 고객")
			void deletedCustomer() {
				// given
				UUID userPublicId = UUID.randomUUID();
				when(customerRepository.findByUserPublicIdAndDeletedAtNull(userPublicId))
					.thenReturn(Optional.empty());

				// when & then
				IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class,
					() -> customerService.getCustomerByUserPublicId(userPublicId)
				);

				assertEquals("잘못된 유저 아이디 입니다.", exception.getMessage());
				verify(customerRepository, times(1)).findByUserPublicIdAndDeletedAtNull(userPublicId);
			}
		}
	}
}
