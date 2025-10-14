package com.sparta.delivery.backend.owner.dto;

import com.sparta.delivery.backend.global.validation.annotation.ValidPassword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ReqChangePasswordDto", description = "비밀번호 변경 요청")
public class ReqChangePasswordDto {

	@Schema(description = "현재 비밀번호", example = "OldPassword123!")
	@NotBlank(message = "현재 비밀번호는 필수입니다.")
	private String currentPassword;

	@Schema(description = "새 비밀번호", example = "NewPassword123!")
	@ValidPassword
	private String newPassword;

	@Schema(description = "새 비밀번호 확인", example = "NewPassword123!")
	@NotBlank(message = "새 비밀번호 확인은 필수입니다.")
	private String newPasswordConfirm;
}
