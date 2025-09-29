package com.sparta.delivery.backend.common;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

/**
 * 현재 로그인한 사용자의 ID를 반환하여
 * @CreatedBy, @LastModifiedBy 필드 자동 채우기에 사용
 * 제네릭 <Long>은 BaseEntity 필드 타입과 일치해야 함
 */
public class LoginUserAuditorAware implements AuditorAware<Long> {

	@Override
	public Optional<Long> getCurrentAuditor() {
		// 현재 로그인한 사용자 ID 반환
		// SecurityContext를 사용하는 경우:
		// Long userId = ((UserDetails) SecurityContextHolder.getContext()
		// .getAuthentication().getPrincipal()).getId();
		// return Optional.of(userId);
		return Optional.empty();
	}

}
