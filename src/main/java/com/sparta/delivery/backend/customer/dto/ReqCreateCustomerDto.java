package com.sparta.delivery.backend.customer.dto;

import com.sparta.delivery.backend.global.annotation.validate.ValidPassword;
import com.sparta.delivery.backend.global.annotation.validate.ValidUsername;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqCreateCustomerDto {

	@ValidUsername
	private String username;

	@ValidPassword
	private String password;

	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 50, message = "닉네임은 최대 50자까지 가능합니다.")
	private String nickname;

	@Email(message = "올바른 이메일 형식이 아닙니다.")
	private String email;

	@Size(max = 20, message = "전화번호는 최대 20자까지 가능합니다.")
	private String phoneNumber;

	@NotBlank(message = "이메일 인증 토큰은 필수입니다.")
	private String emailVerificationToken;
}
