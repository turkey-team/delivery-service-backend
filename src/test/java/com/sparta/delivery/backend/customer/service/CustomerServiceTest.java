package com.sparta.delivery.backend.customer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.delivery.backend.customer.dto.ReqChangePasswordDto;
import com.sparta.delivery.backend.customer.dto.ReqPasswordResetDto;
import com.sparta.delivery.backend.customer.dto.ReqPasswordResetRequestDto;
import com.sparta.delivery.backend.customer.dto.ReqUpdateCustomerDto;
import com.sparta.delivery.backend.customer.dto.ResGetCustomerDto;
import com.sparta.delivery.backend.customer.dto.ResGetMyCustomerDto;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.global.infra.email.EmailSender;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;
import com.sparta.delivery.backend.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
	@InjectMocks
	private CustomerService customerService;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserDetailsImpl userDetails;

	@Mock
	private RedisTemplate redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@Mock
	private EmailSender emailSender;

	private Customer customer;
	private User user;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.username("testuser")
			.password("OldEncodedPassword1!")
			.role(UserRoleEnum.CUSTOMER)
			.build();

		customer = Customer.builder()
			.user(user)
			.nickname("테스트닉네임")
			.email("test@example.com")
			.phoneNumber("010-1234-5678")
			.build();

		lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
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

	@Nested
	@DisplayName("고객 정보 수정 테스트")
	class UpdateCurrentCustomerTest {

		@Mock
		private User mockUser;

		@Test
		@DisplayName("성공 - 닉네임 수정")
		void success() {
			// given
			Long userId = 1L;
			String newNickname = "새로운닉네임";
			ReqUpdateCustomerDto requestDto = new ReqUpdateCustomerDto();
			ReflectionTestUtils.setField(requestDto, "nickname", newNickname);

			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.of(customer));

			// when
			customerService.updateCurrentCustomer(userDetails, requestDto);

			// then
			verify(customerRepository, times(1)).findByUserIdAndDeletedAtIsNull(userId);
			assertEquals(newNickname, customer.getNickname());
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 고객")
		void customerNotFound() {
			// given
			Long userId = 999L;
			ReqUpdateCustomerDto requestDto = new ReqUpdateCustomerDto();

			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.empty());

			// when & then
			assertThrows(
				IllegalArgumentException.class,
				() -> customerService.updateCurrentCustomer(userDetails, requestDto)
			);
		}
	}

	@Nested
	@DisplayName("비밀번호 변경 테스트")
	class ChangePasswordTest {

		@Test
		@DisplayName("성공 - 비밀번호 변경")
		void success() {
			// given
			Long userId = 1L;
			String currentPassword = "OldEncodedPassword1!";
			String newPassword = "NewPassword123!";
			String newEncodedPassword = "NewEncodedPassword123!";

			ReqChangePasswordDto requestDto = new ReqChangePasswordDto();
			ReflectionTestUtils.setField(requestDto, "currentPassword", currentPassword);
			ReflectionTestUtils.setField(requestDto, "newPassword", newPassword);
			ReflectionTestUtils.setField(requestDto, "newPasswordConfirm", newPassword);

			when(userDetails.getId()).thenReturn(userId);
			when(userRepository.findById(userId)).thenReturn(Optional.of(user));
			when(passwordEncoder.matches(currentPassword, user.getPassword())).thenReturn(true);
			when(passwordEncoder.encode(newPassword)).thenReturn(newEncodedPassword);

			// when
			customerService.changePassword(userDetails, requestDto);

			// then
			verify(userRepository, times(1)).findById(userId);
			verify(passwordEncoder, times(1)).matches(currentPassword, "OldEncodedPassword1!");
			assertEquals(user.getPassword(), newEncodedPassword);
		}

		@Test
		@DisplayName("실패 - 현재 비밀번호 불일치")
		void wrongCurrentPassword() {
			// given
			Long userId = 1L;
			String wrongPassword = "WrongPassword123!";
			String newPassword = "NewPassword123!";

			ReqChangePasswordDto requestDto = new ReqChangePasswordDto();
			ReflectionTestUtils.setField(requestDto, "currentPassword", wrongPassword);
			ReflectionTestUtils.setField(requestDto, "newPassword", newPassword);
			ReflectionTestUtils.setField(requestDto, "newPasswordConfirm", newPassword);

			when(userDetails.getId()).thenReturn(userId);
			when(userRepository.findById(userId)).thenReturn(Optional.of(user));
			when(passwordEncoder.matches(wrongPassword, user.getPassword())).thenReturn(false);

			// when & then
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> customerService.changePassword(userDetails, requestDto)
			);

			assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
		}

		@Test
		@DisplayName("실패 - 새 비밀번호 불일치")
		void newPasswordMismatch() {
			// given
			Long userId = 1L;
			String currentPassword = user.getPassword();
			String newPassword = "NewPassword123!";
			String differentPassword = "DifferentPassword123!";

			ReqChangePasswordDto requestDto = new ReqChangePasswordDto();
			ReflectionTestUtils.setField(requestDto, "currentPassword", currentPassword);
			ReflectionTestUtils.setField(requestDto, "newPassword", newPassword);
			ReflectionTestUtils.setField(requestDto, "newPasswordConfirm", differentPassword);

			when(userDetails.getId()).thenReturn(userId);
			when(userRepository.findById(userId)).thenReturn(Optional.of(user));
			when(passwordEncoder.matches(currentPassword, user.getPassword())).thenReturn(true);

			// when & then
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> customerService.changePassword(userDetails, requestDto)
			);

			assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 사용자")
		void userNotFound() {
			// given
			Long userId = 999L;
			ReqChangePasswordDto requestDto = new ReqChangePasswordDto();

			when(userDetails.getId()).thenReturn(userId);
			when(userRepository.findById(userId)).thenReturn(Optional.empty());

			// when & then
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> customerService.changePassword(userDetails, requestDto)
			);

			assertEquals("잘못된 유저 아이디 입니다.", exception.getMessage());
		}
	}

	@Nested
	@DisplayName("비밀번호 재설정 요청 테스트")
	class RequestPasswordResetTest {

		@Test
		@DisplayName("성공 - 비밀번호 재설정 요청")
		void success() {
			// given
			String email = "test@example.com";
			ReqPasswordResetRequestDto requestDto = new ReqPasswordResetRequestDto();
			ReflectionTestUtils.setField(requestDto, "email", email);

			when(customerRepository.findByEmailAndDeletedAtIsNull(email))
				.thenReturn(Optional.of(customer));

			// when
			customerService.requestPasswordReset(requestDto);

			// then
			verify(customerRepository, times(1)).findByEmailAndDeletedAtIsNull(email);
			verify(valueOperations, times(1)).set(anyString(), anyString(), eq(30L), eq(TimeUnit.MINUTES));
			verify(emailSender, times(1)).sendMail(eq(email), anyString(), anyString());
		}

		@Test
		@DisplayName("성공 - 존재하지 않는 이메일 (보안상 동일 응답)")
		void emailNotFound() {
			// given
			String email = "notfound@example.com";
			ReqPasswordResetRequestDto requestDto = new ReqPasswordResetRequestDto();
			ReflectionTestUtils.setField(requestDto, "email", email);

			when(customerRepository.findByEmailAndDeletedAtIsNull(email))
				.thenReturn(Optional.empty());

			// when
			customerService.requestPasswordReset(requestDto);

			// then
			verify(customerRepository, times(1)).findByEmailAndDeletedAtIsNull(email);
			verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
			verify(emailSender, never()).sendMail(anyString(), anyString(), anyString());
		}
	}

	@Nested
	@DisplayName("비밀번호 재설정 확인 테스트")
	class ResetPasswordTest {
		@Test
		@DisplayName("성공 - 비밀번호 재설정")
		void success() {
			// given
			String email = "test@example.com";
			String token = "valid-token-123";
			String newPassword = "NewPassword123!";
			String newEncodedPassword = "encodedNewPassword";

			ReqPasswordResetDto requestDto = new ReqPasswordResetDto();
			ReflectionTestUtils.setField(requestDto, "email", email);
			ReflectionTestUtils.setField(requestDto, "token", token);
			ReflectionTestUtils.setField(requestDto, "newPassword", newPassword);
			ReflectionTestUtils.setField(requestDto, "newPasswordConfirm", newPassword);

			when(valueOperations.get(anyString())).thenReturn(token);
			when(customerRepository.findByEmailAndDeletedAtIsNull(email))
				.thenReturn(Optional.of(customer));
			when(passwordEncoder.encode(newPassword)).thenReturn(newEncodedPassword);

			// when
			customerService.resetPassword(requestDto);

			// then
			verify(valueOperations, times(1)).get(anyString());
			verify(customerRepository, times(1)).findByEmailAndDeletedAtIsNull(email);
			verify(passwordEncoder, times(1)).encode(newPassword);
			verify(redisTemplate, times(1)).delete(anyString());
			assertEquals(newEncodedPassword, user.getPassword());
		}

		@Test
		@DisplayName("실패 - 유효하지 않은 토큰")
		void invalidToken() {
			// given
			String email = "test@example.com";
			String wrongToken = "wrong-token";
			String storedToken = "valid-token-123";

			ReqPasswordResetDto requestDto = new ReqPasswordResetDto();
			ReflectionTestUtils.setField(requestDto, "email", email);
			ReflectionTestUtils.setField(requestDto, "token", wrongToken);
			ReflectionTestUtils.setField(requestDto, "newPassword", "NewPassword123!");
			ReflectionTestUtils.setField(requestDto, "newPasswordConfirm", "NewPassword123!");

			when(valueOperations.get(anyString())).thenReturn(storedToken);

			// when & then
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> customerService.resetPassword(requestDto)
			);

			assertEquals("유효하지 않은 토큰입니다.", exception.getMessage());
		}

		@Test
		@DisplayName("실패 - 만료된 토큰")
		void expiredToken() {
			// given
			String email = "test@example.com";
			ReqPasswordResetDto requestDto = new ReqPasswordResetDto();
			ReflectionTestUtils.setField(requestDto, "email", email);
			ReflectionTestUtils.setField(requestDto, "token", "expired-token");

			when(valueOperations.get(anyString())).thenReturn(null);

			// when & then
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> customerService.resetPassword(requestDto)
			);

			assertEquals("유효하지 않은 토큰입니다.", exception.getMessage());
		}

		@Test
		@DisplayName("실패 - 새 비밀번호 불일치")
		void passwordMismatch() {
			// given
			String email = "test@example.com";
			String token = "valid-token-123";

			ReqPasswordResetDto requestDto = new ReqPasswordResetDto();
			ReflectionTestUtils.setField(requestDto, "email", email);
			ReflectionTestUtils.setField(requestDto, "token", token);
			ReflectionTestUtils.setField(requestDto, "newPassword", "NewPassword123!");
			ReflectionTestUtils.setField(requestDto, "newPasswordConfirm", "DifferentPassword123!");

			when(valueOperations.get(anyString())).thenReturn(token);

			// when & then
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> customerService.resetPassword(requestDto)
			);

			assertEquals("새 비밀번호가 일치하지 않습니다.", exception.getMessage());
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 사용자")
		void userNotFound() {
			// given
			String email = "notfound@example.com";
			String token = "valid-token-123";
			String newPassword = "NewPassword123!";

			ReqPasswordResetDto requestDto = new ReqPasswordResetDto();
			ReflectionTestUtils.setField(requestDto, "email", email);
			ReflectionTestUtils.setField(requestDto, "token", token);
			ReflectionTestUtils.setField(requestDto, "newPassword", newPassword);
			ReflectionTestUtils.setField(requestDto, "newPasswordConfirm", newPassword);

			when(valueOperations.get(anyString())).thenReturn(token);
			when(customerRepository.findByEmailAndDeletedAtIsNull(email))
				.thenReturn(Optional.empty());

			// when & then
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> customerService.resetPassword(requestDto)
			);

			assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
		}
	}

	@Nested
	@DisplayName("고객 삭제 테스트 (본인)")
	class DeleteCurrentCustomerTest {

		@Test
		@DisplayName("성공 - 본인 계정 삭제")
		void success() {
			// given
			Long userId = 1L;
			ReflectionTestUtils.setField(user, "id", userId);

			when(userDetails.getId()).thenReturn(userId);
			when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
				.thenReturn(Optional.of(customer));

			// when
			customerService.deleteCurrentCustomer(userDetails);

			// then
			verify(customerRepository, times(1)).findByUserIdAndDeletedAtIsNull(userId);
			assertNotNull(customer.getDeletedAt());
			assertEquals(userId, customer.getDeletedBy());
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
				when(customerRepository.findByUserIdAndDeletedAtIsNull(userId))
					.thenReturn(Optional.empty());

				// when & then
				IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class,
					() -> customerService.deleteCurrentCustomer(userDetails)
				);
			}
		}
	}

	@Nested
	@DisplayName("고객 삭제 테스트 (관리자)")
	class DeleteCustomerByManagerTest {

		@Test
		@DisplayName("성공 - 관리자가 고객 삭제")
		void success() {
			// given
			Long managerId = 2L;
			UUID customerPublicId = UUID.randomUUID();

			when(userDetails.getId()).thenReturn(managerId);
			when(customerRepository.findByUserPublicIdAndDeletedAtNull(customerPublicId))
				.thenReturn(Optional.of(customer));

			// when
			customerService.deleteCustomerByManager(userDetails, customerPublicId);

			// then
			verify(customerRepository, times(1)).findByUserPublicIdAndDeletedAtNull(customerPublicId);
			assertNotNull(customer.getDeletedAt());
			assertEquals(managerId, customer.getDeletedBy());
		}

		@Nested
		@DisplayName("실패")
		class FailCase {

			@Test
			@DisplayName("존재하지 않는 고객 공개 ID")
			void customerNotFound() {
				// given
				Long managerId = 2L;
				UUID invalidPublicId = UUID.randomUUID();

				when(customerRepository.findByUserPublicIdAndDeletedAtNull(invalidPublicId))
					.thenReturn(Optional.empty());

				// when & then
				IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class,
					() -> customerService.deleteCustomerByManager(userDetails, invalidPublicId)
				);
			}
		}
	}
}
