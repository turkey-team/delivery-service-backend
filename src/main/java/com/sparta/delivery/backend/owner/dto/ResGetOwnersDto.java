package com.sparta.delivery.backend.owner.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.owner.entity.Owner;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "ResGetOwnersDto", description = "점주 목록 조회 응답 (관리자용)")
public class ResGetOwnersDto {
	@Schema(description = "점주 사용자 공개 ID", example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID ownerUserPublicId;

	@Schema(description = "사용자 아이디", example = "owner1")
	private String username;

	@Schema(description = "닉네임", example = "홍길동")
	private String nickname;

	@Schema(description = "이메일", example = "owner@example.com")
	private String email;

	@Schema(description = "전화번호", example = "010-1234-5678")
	private String phoneNumber;

	@Schema(description = "가입일시", example = "2025-01-15T10:30:00Z")
	private Instant createdAt;

	@Schema(description = "수정일시", example = "2025-01-20T14:20:00Z")
	private Instant updatedAt;

	public static ResGetOwnersDto from(Owner owner) {
		return ResGetOwnersDto.builder()
			.ownerUserPublicId(owner.getUser().getPublicId())
			.username(owner.getUser().getUsername())
			.nickname(owner.getNickname())
			.email(owner.getEmail())
			.phoneNumber(owner.getPhoneNumber())
			.createdAt(owner.getCreatedAt())
			.updatedAt(owner.getUpdatedAt())
			.build();
	}
}