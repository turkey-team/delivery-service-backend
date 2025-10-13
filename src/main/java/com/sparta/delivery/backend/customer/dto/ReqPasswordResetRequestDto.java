package com.sparta.delivery.backend.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ReqPasswordResetRequestDto", description = "비밀번호 재설정 요청")
public class ReqPasswordResetRequestDto {

	@Schema(
		description = "이메일",
		example = "customer@example.com",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 320, message = "이메일은 최대 320자까지 가능합니다.")
	private String email;
}
