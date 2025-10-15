package com.sparta.delivery.backend.manager.dto;

import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqUpdateRoleDto {

	private UserRoleEnum role;

}
