package com.sparta.delivery.backend.manager.dto;

import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqUpdateRoleDto {

	@NotNull(message = "role은 필수입니다.")
	private UserRoleEnum role;

}
