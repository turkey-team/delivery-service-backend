package com.sparta.delivery.backend.manager.dto;

import java.util.UUID;

import com.sparta.delivery.backend.manager.entity.Manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResGetManagerSummaryDto {

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

	public static ResGetManagerSummaryDto from(Manager manager) {
		return ResGetManagerSummaryDto.builder()
			.managerUserPublicId(manager.getUserPublicId())
			.username(manager.getUsername())
			.name(manager.getName())
			.email(manager.getEmail())
			.phoneNumber(manager.getFormattedPhoneNumber())
			.role(manager.getUserRole().name())
			.build();
	}

}
