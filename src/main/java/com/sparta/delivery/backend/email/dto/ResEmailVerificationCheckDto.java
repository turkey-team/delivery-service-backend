package com.sparta.delivery.backend.email.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResEmailVerificationCheckDto {
	private String message;
	private String verificationToken;  // 회원가입 시 사용할 토큰
	private boolean success;

	public static ResEmailVerificationCheckDto success(String message, String token) {
		return new ResEmailVerificationCheckDto(message, token, true);
	}

	public static ResEmailVerificationCheckDto failure(String message) {
		return new ResEmailVerificationCheckDto(message, null, false);
	}
}
