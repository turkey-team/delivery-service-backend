package com.sparta.delivery.backend.customer.dto;

import com.sparta.delivery.backend.global.validation.annotation.ValidPassword;
import com.sparta.delivery.backend.global.validation.annotation.ValidUsername;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ReqCreateCustomerDto", description = "고객 회원가입 요청")
public class ReqCreateCustomerDto {

	@Schema(description = "사용자 아이디", example = "customer1", requiredMode = Schema.RequiredMode.REQUIRED)
	@ValidUsername
	private String username;

	@Schema(description = "비밀번호", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
	@ValidPassword
	private String password;

	@Schema(description = "닉네임", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 50, message = "닉네임은 최대 50자까지 가능합니다.")
	private String nickname;

	@Schema(description = "이메일", example = "customer@example.com", maxLength = 320, requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 320, message = "이메일은 최대 320자까지 가능합니다.")
	private String email;

	@Schema(description = "전화번호", example = "010-1234-5678", maxLength = 20)
	@Size(max = 20, message = "전화번호는 최대 20자까지 가능합니다.")
	private String phoneNumber;

	@Schema(description = "이메일 인증 토큰", example = "abc123def456", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "이메일 인증 토큰은 필수입니다.")
	private String emailVerificationToken;
}
