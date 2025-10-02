package com.sparta.delivery.backend.manager.dto;

import com.sparta.delivery.backend.global.annotation.validate.ValidPassword;
import com.sparta.delivery.backend.global.annotation.validate.ValidUsername;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqCreateManagerDto {

	@ValidUsername
	private String username;

	@ValidPassword
	private String password;

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 320, message = "이메일은 최대 320자까지 가능합니다.")
	private String email;

	@NotBlank(message = "매니저 이름은 필수입니다.")
	private String name;

	@Size(max = 20, message = "전화번호는 최대 20자까지 가능합니다.")
	private String phoneNumber;
}
