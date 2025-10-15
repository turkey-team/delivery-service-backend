package com.sparta.delivery.backend.manager.dto;

import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqUpdateRoleDto {

	@NotBlank(message = "role은 필수입니다.")
	private UserRoleEnum role;

}
