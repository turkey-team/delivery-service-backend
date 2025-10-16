package com.sparta.delivery.backend.customer.dto;

import com.sparta.delivery.backend.global.validation.annotation.ValidEmail;
import com.sparta.delivery.backend.global.validation.annotation.ValidPassword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ReqPasswordResetDto", description = "비밀번호 재설정")
public class ReqPasswordResetDto {

	@Schema(description = "이메일", example = "customer@example.com")
	@NotBlank(message = "이메일은 필수입니다.")
	@ValidEmail
	private String email;

	@Schema(description = "재설정 토큰", example = "abc123-def456-ghi789")
	@NotBlank(message = "토큰은 필수입니다.")
	private String token;

	@Schema(description = "새 비밀번호", example = "NewPassword123!")
	@ValidPassword
	private String newPassword;

	@Schema(description = "새 비밀번호 확인", example = "NewPassword123!")
	@NotBlank(message = "새 비밀번호 확인은 필수입니다.")
	private String newPasswordConfirm;
}
