package com.sparta.delivery.backend.email.service;

import static com.sparta.delivery.backend.global.infra.redis.RedisKeyConstants.*;

import com.sparta.delivery.backend.email.dto.ReqEmailVerificationCheckDto;
import com.sparta.delivery.backend.email.dto.ReqEmailVerificationDto;
import com.sparta.delivery.backend.email.dto.ResEmailVerificationCheckDto;
import com.sparta.delivery.backend.email.dto.ResEmailVerificationDto;
import com.sparta.delivery.backend.global.infra.email.EmailSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {
	private static final long CODE_EXPIRATION_MINUTES = 5;
	private static final long TOKEN_EXPIRATION_HOUR = 24;
    private static final SecureRandom RANDOM = new SecureRandom();
    
    private final StringRedisTemplate redisTemplate;
    private final EmailSender emailSender;

    public ResEmailVerificationDto sendVerificationCode(ReqEmailVerificationDto requestDto) {
        try {
            String code = generateVerificationCode();

            String key = EMAIL_VERIFY_PREFIX + requestDto.getEmail();
            redisTemplate.opsForValue().set(key, code, CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);

            sendEmail(requestDto.getEmail(), code);

            return ResEmailVerificationDto.success("인증 코드가 발송되었습니다.");
            
        } catch (Exception e) {
			log.error("메시지 발송 실패", e);
            return ResEmailVerificationDto.failure("인증 코드 발송에 실패했습니다.");
        }
    }

	public ResEmailVerificationCheckDto verifyCode(ReqEmailVerificationCheckDto requestDto) {
		String key = EMAIL_VERIFY_PREFIX + requestDto.getEmail();
		String storedCode = redisTemplate.opsForValue().get(key);

		if (storedCode == null) {
			log.warn("인증 코드 만료 또는 존재하지 않음: email={}", requestDto.getEmail());
			return ResEmailVerificationCheckDto.failure("인증 코드가 만료되었습니다.");
		}

		if (!storedCode.equals(requestDto.getCode())) {
			log.warn("인증 코드 불일치: email={}", requestDto.getEmail());
			return ResEmailVerificationCheckDto.failure("인증 코드가 올바르지 않습니다.");
		}

		// 인증 성공 → 인증 코드 삭제
		redisTemplate.delete(key);

		// 회원가입용 토큰 생성 및 저장
		String verificationToken = generateVerificationToken();
		String tokenKey = EMAIL_VERIFIED_PREFIX + requestDto.getEmail();
		redisTemplate.opsForValue().set(tokenKey, verificationToken, TOKEN_EXPIRATION_HOUR, TimeUnit.HOURS);

		log.info("이메일 인증 성공: email={}, token={}", requestDto.getEmail(), verificationToken);
		return ResEmailVerificationCheckDto.success("이메일 인증이 완료되었습니다.", verificationToken);
	}

    private String generateVerificationCode() {
        int code = RANDOM.nextInt(900000) + 100000; // 100000 ~ 999999
        return String.valueOf(code);
    }

	private String generateVerificationToken() {
		return UUID.randomUUID().toString();
	}

    private void sendEmail(String to, String code) {
        log.info("이메일 발송 시뮬레이션: to={}, code={}", to, code);

        String subject = "[칠면조 배달 서비스] 이메일 인증 코드";
        String content = String.format("인증 코드: %s\n\n유효시간: 5분", code);
        emailSender.sendMail(to, subject, content);
    }
}
