package com.sparta.delivery.backend.global.infra.redis;

public final class RedisKeyConstants {

	private RedisKeyConstants() {
		// 인스턴스화 방지
	}

	// ===== 이메일 인증 =====
	public static final String EMAIL_VERIFY_PREFIX = "email:verify:";      // 인증 코드
	public static final String EMAIL_VERIFIED_PREFIX = "email:verified:";  // 인증 완료 토큰

	// ===== 블랙리스트 (토큰) =====
	public static final String BLACKLIST_LOGOUT_PREFIX = "BL:LO:";        // 로그아웃된 토큰
	public static final String BLACKLIST_ROTATE_PREFIX = "BL:RT:";      // 갱신된(무효화된) 토큰

}