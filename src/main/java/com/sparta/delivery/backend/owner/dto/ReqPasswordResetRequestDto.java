package com.sparta.delivery.backend.owner.dto;

import com.sparta.delivery.backend.global.validation.annotation.ValidEmail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ReqPasswordResetRequestDto", description = "비밀번호 재설정 요청")
public class ReqPasswordResetRequestDto {

	@Schema(description = "이메일", example = "owner@example.com")
	@NotBlank(message = "이메일은 필수입니다.")
	@ValidEmail
	private String email;
}
