package com.sparta.delivery.backend.global;

import static com.sparta.delivery.backend.global.infra.redis.RedisKeyConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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

import com.sparta.delivery.backend.global.verification.EmailVerificationTokenValidator;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationTokenValidatorTest {

	@InjectMocks
	private EmailVerificationTokenValidator emailVerificationTokenValidator;

	@Mock
	private StringRedisTemplate redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	private String email;
	private String token;

	@BeforeEach
	void setUp() {
		given(redisTemplate.opsForValue()).willReturn(valueOperations);

		email = "test@example.com";
		token = "test-token-uuid-12345";
	}

	@Nested
	@DisplayName("토큰 검증 및 소비 테스트")
	class ValidateAndConsumeTokenTest {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			String redisKey = EMAIL_VERIFIED_PREFIX + email;
			given(valueOperations.get(redisKey)).willReturn(token);
			given(redisTemplate.delete(redisKey)).willReturn(true);

			// when & then
			assertDoesNotThrow(() -> emailVerificationTokenValidator.validateAndConsumeToken(email, token));

			then(valueOperations).should(times(1)).get(redisKey);
			then(redisTemplate).should(times(1)).delete(redisKey);
		}

		@Nested
		@DisplayName("실패")
		class FailCase {

			@Test
			@DisplayName("토큰 만료")
			void tokenExpired() {
				// given
				String redisKey = EMAIL_VERIFIED_PREFIX + email;
				given(valueOperations.get(redisKey)).willReturn(null);

				// when & then
				IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class,
					() -> emailVerificationTokenValidator.validateAndConsumeToken(email, token)
				);

				assertEquals("이메일 인증이 만료되었습니다. 다시 인증해주세요.", exception.getMessage());
				then(valueOperations).should(times(1)).get(redisKey);
				then(redisTemplate).should(never()).delete(redisKey);
			}
		}
	}
}