package com.sparta.delivery.backend.user.entity;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
	MANAGER(Authority.MANAGER),
	CUSTOMER(Authority.CUSTOMER),
	OWNER(Authority.OWNER);

	private final String authority;

	UserRoleEnum(String authority) {
		this.authority = authority;
	}

	public static class Authority {
		public static final String MANAGER = "ROLE_MANAGER";
		public static final String CUSTOMER = "ROLE_CUSTOMER";
		public static final String OWNER = "ROLE_OWNER";
	}
}