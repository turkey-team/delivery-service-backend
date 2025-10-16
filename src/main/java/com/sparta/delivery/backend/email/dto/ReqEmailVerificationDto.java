package com.sparta.delivery.backend.email.dto;

import com.sparta.delivery.backend.global.validation.annotation.ValidEmail;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReqEmailVerificationDto {

	@NotBlank(message = "이메일은 필수입니다")
	@ValidEmail
	private String email;
}
