package com.sparta.delivery.backend.manager.dto;

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
@Schema(name = "ReqCreateManagerDto", description = "매니저 회원가입 요청")
public class ReqCreateManagerDto {

	@Schema(description = "사용자 아이디", example = "manager1")
	@ValidUsername
	private String username;

	@Schema(description = "비밀번호", example = "Password123!")
	@ValidPassword
	private String password;

	@Schema(description = "이메일", example = "manager@example.com")
	@NotBlank(message = "이메일은 필수입니다.")
	@ValidEmail
	@Size(max = 320, message = "이메일은 최대 320자까지 가능합니다.")
	private String email;

	@Schema(description = "매니저 이름", example = "김매니저")
	@NotBlank(message = "매니저 이름은 필수입니다.")
	private String name;

	@Schema(description = "전화번호", example = "01012345678")
	@Size(max = 20, message = "전화번호는 최대 20자까지 가능합니다.")
	@ValidPhoneNumber
	private String phoneNumber;
}
