package com.sparta.delivery.backend.email.dto;

import com.sparta.delivery.backend.global.validation.annotation.ValidEmail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReqEmailVerificationCheckDto {

	@NotBlank(message = "이메일은 필수입니다")
	@ValidEmail
	private String email;

	@NotBlank(message = "인증 코드는 필수입니다")
	@Pattern(regexp = "^\\d{6}$", message = "인증 코드는 6자리 숫자여야 합니다")
	private String code;
}
