package com.sparta.delivery.backend.manager.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.manager.entity.Manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResGetManagerDetailDto {

	@Schema(description = "관리자 사용자 공개 ID", example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID managerUserPublicId;

	@Schema(description = "사용자 아이디", example = "manager1")
	private String username;

	@Schema(description = "이름", example = "홍길동")
	private String name;

	@Schema(description = "이메일", example = "customer@example.com")
	private String email;

	@Schema(description = "전화번호", example = "010-1234-5678")
	private String phoneNumber;

	@Schema(description = "권한", example = "MASTER")
	private String role;

	@Schema(description = "가입일시", example = "2025-01-15T10:30:00Z")
	private Instant createdAt;

	@Schema(description = "수정일시", example = "2025-01-15T10:30:00Z")
	private Instant updatedAt;

	public static ResGetManagerDetailDto from(Manager manager) {
		return ResGetManagerDetailDto.builder()
			.managerUserPublicId(manager.getUserPublicId())
			.username(manager.getUsername())
			.name(manager.getName())
			.email(manager.getEmail())
			.phoneNumber(manager.getFormattedPhoneNumber())
			.role(manager.getUserRole().name())
			.createdAt(manager.getCreatedAt())
			.updatedAt(manager.getUpdatedAt())
			.build();
	}

}
