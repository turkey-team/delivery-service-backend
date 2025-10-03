package com.sparta.delivery.backend.common;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sparta.delivery.backend.security.UserDetailsImpl;

/**
 * 현재 로그인한 사용자의 ID를 반환하여
 * @CreatedBy, @LastModifiedBy 필드 자동 채우기에 사용
 * 제네릭 <Long>은 BaseEntity 필드 타입과 일치해야 함
 */
@Component
public class LoginUserAuditorAware implements AuditorAware<Long> {

	@Override
	public Optional<Long> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null &&
			authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
			// 현재 로그인한 사용자 ID 반환
			// SecurityContext를 사용하는 경우:
			Long userId = userDetails.getId();
			return Optional.of(userId);
		}

		return Optional.empty();
	}

}