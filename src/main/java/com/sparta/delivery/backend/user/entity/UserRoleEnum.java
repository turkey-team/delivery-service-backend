package com.sparta.delivery.backend.user.entity;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    MANAGER("ROLE_MANAGER"),
    CUSTOMER("ROLE_CUSTOMER"),
    OWNER("ROLE_OWNER");

	UserRoleEnum(String authority) {
		this.authority = authority;
	}

	private final String authority;
}
