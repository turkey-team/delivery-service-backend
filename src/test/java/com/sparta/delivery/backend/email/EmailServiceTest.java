package com.sparta.delivery.backend.email;

import static com.sparta.delivery.backend.global.infra.redis.RedisKeyConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.sparta.delivery.backend.email.dto.ReqEmailVerificationCheckDto;
import com.sparta.delivery.backend.email.dto.ReqEmailVerificationDto;
import com.sparta.delivery.backend.email.dto.ResEmailVerificationCheckDto;
import com.sparta.delivery.backend.email.dto.ResEmailVerificationDto;
import com.sparta.delivery.backend.email.service.EmailVerificationService;
import com.sparta.delivery.backend.global.infra.email.EmailSender;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
	@InjectMocks
	private EmailVerificationService emailVerificationService;

	@Mock
	private StringRedisTemplate redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@Mock
	private EmailSender emailSender;

	private String email;
	private String verificationCode;
	private ReqEmailVerificationDto reqEmailVerificationDto;
	private ReqEmailVerificationCheckDto reqEmailVerificationCheckDto;

	@BeforeEach
	void setUp() {
		// redisTemplate.opsForValue() 호출 시 valueOperations 반환
		given(redisTemplate.opsForValue()).willReturn(valueOperations);

		email = "test@example.com";
		verificationCode = "123456";
		reqEmailVerificationDto = ReqEmailVerificationDto.builder()
			.email(email)
			.build();
		reqEmailVerificationCheckDto = ReqEmailVerificationCheckDto.builder()
			.email(email)
			.code(verificationCode)
			.build();
	}

	@Nested
	@DisplayName("인증 코드 전송 테스트")
	class SendVerificationCodeTest {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			String redisKey = EMAIL_VERIFY_PREFIX + email;
			willDoNothing().given(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
			willDoNothing().given(emailSender).sendMail(anyString(), anyString(), anyString());

			// when
			ResEmailVerificationDto result = emailVerificationService.sendVerificationCode(reqEmailVerificationDto);

			// then
			assertTrue(result.isSuccess());
			assertEquals("인증 코드가 발송되었습니다.", result.getMessage());
			then(valueOperations).should(times(1)).set(eq(redisKey), anyString(), eq(5L), eq(TimeUnit.MINUTES));
			then(emailSender).should(times(1)).sendMail(eq(email), anyString(), anyString());
		}

		@Nested
		@DisplayName("실패")
		class FailCase {

			@Test
			@DisplayName("이메일 전송 실패")
			void emailSendFailed() {
				// given
				willDoNothing().given(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
				willThrow(new RuntimeException("메일 전송 실패"))
					.given(emailSender).sendMail(anyString(), anyString(), anyString());

				// when
				ResEmailVerificationDto result = emailVerificationService.sendVerificationCode(reqEmailVerificationDto);

				// then
				assertFalse(result.isSuccess());
				assertEquals("인증 코드 발송에 실패했습니다.", result.getMessage());
			}

			@Test
			@DisplayName("Redis 저장 실패")
			void redisSaveFailed() {
				// given
				willThrow(new RuntimeException("Redis 저장 실패"))
					.given(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

				// when
				ResEmailVerificationDto result = emailVerificationService.sendVerificationCode(reqEmailVerificationDto);

				// then
				assertFalse(result.isSuccess());
				assertEquals("인증 코드 발송에 실패했습니다.", result.getMessage());
			}
		}
	}

	@Nested
	@DisplayName("인증 코드 검증 테스트")
	class VerifyCodeTest {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			String redisKey = EMAIL_VERIFY_PREFIX + email;
			String tokenKey = EMAIL_VERIFIED_PREFIX + email;
			given(valueOperations.get(redisKey)).willReturn(verificationCode);
			given(redisTemplate.delete(redisKey)).willReturn(true);
			willDoNothing().given(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

			// when
			ResEmailVerificationCheckDto result = emailVerificationService.verifyCode(reqEmailVerificationCheckDto);

			// then
			assertTrue(result.isSuccess());
			assertEquals("이메일 인증이 완료되었습니다.", result.getMessage());
			assertNotNull(result.getVerificationToken());
			then(valueOperations).should(times(1)).get(redisKey);
			then(redisTemplate).should(times(1)).delete(redisKey);
			then(valueOperations).should(times(1)).set(eq(tokenKey), anyString(), eq(24L), eq(TimeUnit.HOURS));
		}

		@Nested
		@DisplayName("실패")
		class FailCase {

			@Test
			@DisplayName("인증 코드 불일치")
			void codeNotMatch() {
				// given
				String redisKey = EMAIL_VERIFY_PREFIX + email;
				String wrongCode = "654321";
				ReqEmailVerificationCheckDto wrongRequest = ReqEmailVerificationCheckDto.builder()
					.email(email)
					.code(wrongCode)
					.build();
				given(valueOperations.get(redisKey)).willReturn(verificationCode);

				// when
				ResEmailVerificationCheckDto result = emailVerificationService.verifyCode(wrongRequest);

				// then
				assertFalse(result.isSuccess());
				assertEquals("인증 코드가 올바르지 않습니다.", result.getMessage());
				assertNull(result.getVerificationToken());
				then(valueOperations).should(times(1)).get(redisKey);
				then(redisTemplate).should(never()).delete(redisKey);
			}

			@Test
			@DisplayName("만료된 인증 코드")
			void expiredCode() {
				// given
				String redisKey = EMAIL_VERIFY_PREFIX + email;
				given(valueOperations.get(redisKey)).willReturn(null);

				// when
				ResEmailVerificationCheckDto result = emailVerificationService.verifyCode(reqEmailVerificationCheckDto);

				// then
				assertFalse(result.isSuccess());
				assertEquals("인증 코드가 만료되었습니다.", result.getMessage());
				assertNull(result.getVerificationToken());
				then(valueOperations).should(times(1)).get(redisKey);
				then(redisTemplate).should(never()).delete(redisKey);
			}

			@Test
			@DisplayName("존재하지 않는 인증 요청")
			void verificationNotFound() {
				// given
				String redisKey = EMAIL_VERIFY_PREFIX + email;
				given(valueOperations.get(redisKey)).willReturn(null);

				// when
				ResEmailVerificationCheckDto result = emailVerificationService.verifyCode(reqEmailVerificationCheckDto);

				// then
				assertFalse(result.isSuccess());
				assertEquals("인증 코드가 만료되었습니다.", result.getMessage());
				assertNull(result.getVerificationToken());
				then(valueOperations).should(times(1)).get(redisKey);
			}
		}
	}
}