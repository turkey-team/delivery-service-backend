package com.sparta.delivery.backend.owner.dto;

import com.sparta.delivery.backend.global.validation.annotation.ValidEmail;
import com.sparta.delivery.backend.global.validation.annotation.ValidPassword;
import com.sparta.delivery.backend.global.validation.annotation.ValidPhoneNumber;
import com.sparta.delivery.backend.global.validation.annotation.ValidUsername;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ReqCreateOwnerDto", description = "점주 회원가입 요청")
public class ReqCreateOwnerDto {

	@Schema(description = "사용자 아이디", example = "owner1")
	@ValidUsername
	private String username;

	@Schema(description = "비밀번호", example = "Password123!")
	@ValidPassword
	private String password;

	@Schema(description = "닉네임", example = "홍길동")
	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 50, message = "닉네임은 최대 50자까지 가능합니다.")
	private String nickname;

	@Schema(description = "이메일", example = "owner@example.com")
	@NotBlank(message = "이메일은 필수입니다.")
	@ValidEmail
	@Size(max = 320, message = "이메일은 최대 320자까지 가능합니다.")
	private String email;

	@Schema(description = "전화번호", example = "010-1234-5678")
	@Size(max = 20, message = "전화번호는 최대 20자까지 가능합니다.")
	@ValidPhoneNumber
	private String phoneNumber;

	@Schema(description = "이메일 인증 토큰", example = "550e8400-e29b-41d4-a716-446655440000")
	@NotBlank(message = "이메일 인증 토큰은 필수입니다.")
	private String emailVerificationToken;
}


