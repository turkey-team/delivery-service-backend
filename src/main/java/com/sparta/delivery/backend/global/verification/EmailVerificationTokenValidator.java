package com.sparta.delivery.backend.global.verification;

import static com.sparta.delivery.backend.global.infra.redis.RedisKeyConstants.*;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailVerificationTokenValidator {

	private final StringRedisTemplate redisTemplate;

	/**
	 * 이메일 인증 토큰 검증 및 소비
	 * @throws IllegalArgumentException 토큰이 유효하지 않거나 만료된 경우
	 */
	public void validateAndConsumeToken(String email, String token) {
		String key = EMAIL_VERIFIED_PREFIX + email;
		String storedToken = redisTemplate.opsForValue().get(key);

		if (storedToken == null) {
			throw new IllegalArgumentException("이메일 인증이 만료되었습니다. 다시 인증해주세요.");
		}

		if (!storedToken.equals(token)) {
			throw new IllegalArgumentException("유효하지 않은 인증 토큰입니다.");
		}

		redisTemplate.delete(key);
		log.info("인증 토큰 검증 성공: email={}", email);
	}
}
