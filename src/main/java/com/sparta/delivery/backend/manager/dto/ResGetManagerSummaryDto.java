package com.sparta.delivery.backend.manager.dto;

import java.util.UUID;

import com.sparta.delivery.backend.manager.entity.Manager;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResGetManagerSummaryDto {

	private UUID managerUserPublicId;

	private String username;

	private String name;

	private String email;

	private String phoneNumber;

	private String role;

	public static ResGetManagerSummaryDto from(Manager manager) {
		return ResGetManagerSummaryDto.builder()
			.managerUserPublicId(manager.getUserPublicId())
			.username(manager.getUsername())
			.name(manager.getName())
			.email(manager.getEmail())
			.phoneNumber(manager.getPhoneNumber())
			.role(manager.getUserRole().name())
			.build();
	}

}
