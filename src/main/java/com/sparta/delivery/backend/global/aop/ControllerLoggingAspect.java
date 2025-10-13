package com.sparta.delivery.backend.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sparta.delivery.backend.security.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

	// AOP 적용 대상 정의
	@Pointcut("within(com.sparta.delivery.backend..*Controller)")
	public void allControllerMethods() {
	}

	@Around("allControllerMethods()")
	public Object logReviewControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {

		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

		if (!(requestAttributes instanceof ServletRequestAttributes)) {
			log.warn("RequestContextHolder에 RequestAttributes가 없습니다 — 스레드: {}",
				Thread.currentThread().getName());
			return joinPoint.proceed();
		}

		HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();

		String requestUrl = request != null ? request.getRequestURI() : "unknown";

		String userId = "anonymous";
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl principal) {
				userId = String.valueOf(principal.getId());
			}
		} catch (Exception e) {
			log.warn("사용자 인증 정보를 가져오는 중 오류 발생: {}", e.getMessage());
		}

		log.info("요청 URL: {}, 사용자 ID: {}", requestUrl, userId);

		Object result;
		long startTime = System.currentTimeMillis();
		try {
			// 실제 컨트롤러 메서드 실행
			result = joinPoint.proceed();
		} catch (Throwable t) {
			log.error("요청 처리 중 예외 발생 - URL: {}, 사용자 ID: {}, 예외: {}", requestUrl, userId, t.toString());
			throw t;
		} finally {
			long duration = System.currentTimeMillis() - startTime;

			if (duration >= 10000) {
				log.warn("처리 시간이 10초 이상 소요됨 - URL: {}, 사용자 ID: {}, 처리 시간: {}ms",
					requestUrl, userId, duration);
			}
		}

		if (!requestUrl.startsWith("/v1/auth")) {
			log.info("요청 완료 URL: {}, 사용자 ID: {}, 응답 결과: {}", requestUrl, userId, result);
		} else {
			log.info("요청 완료 URL: {}, 사용자 ID: {} (응답 결과 미출력)", requestUrl, userId);
		}

		return result;
	}

}
