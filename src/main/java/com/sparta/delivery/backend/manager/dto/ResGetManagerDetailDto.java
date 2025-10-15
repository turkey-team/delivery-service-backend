package com.sparta.delivery.backend.manager.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.manager.entity.Manager;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResGetManagerDetailDto {

	private UUID managerUserPublicId;

	private String username;

	private String name;

	private String email;

	private String phoneNumber;

	private String role;

	private Instant createdAt;

	private Instant updatedAt;

	public static ResGetManagerDetailDto from(Manager manager) {
		return ResGetManagerDetailDto.builder()
			.managerUserPublicId(manager.getUserPublicId())
			.username(manager.getUsername())
			.name(manager.getName())
			.email(manager.getEmail())
			.phoneNumber(manager.getPhoneNumber())
			.role(manager.getUserRole().name())
			.createdAt(manager.getCreatedAt())
			.updatedAt(manager.getUpdatedAt())
			.build();
	}

}
