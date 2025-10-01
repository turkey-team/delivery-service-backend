package com.sparta.delivery.backend.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqCreateCustomerDto {

	@NotBlank(message = "아이디는 필수입니다.")
	@Pattern(
		regexp = "^[a-z0-9]{4,10}$",
		message = "아이디는 4~10자의 소문자 알파벳과 숫자로만 구성되어야 합니다."
	)
	private String username;

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,15}$",
		message = "비밀번호는 8~15자이며, 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
	)
	private String password;

	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 50, message = "닉네임은 최대 50자까지 가능합니다.")
	private String nickname;

	@Email(message = "올바른 이메일 형식이 아닙니다.")
	private String email;

	@Size(max = 20, message = "전화번호는 최대 20자까지 가능합니다.")
	private String phoneNumber;
}
