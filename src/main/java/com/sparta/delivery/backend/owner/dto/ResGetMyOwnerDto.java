package com.sparta.delivery.backend.owner.dto;

import java.time.Instant;

import com.sparta.delivery.backend.owner.entity.Owner;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "ResGetMyOwnerDto", description = "점주 마이페이지 조회")
public class ResGetMyOwnerDto {
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

	public static ResGetMyOwnerDto from(Owner owner) {
		return ResGetMyOwnerDto.builder()
			.username(owner.getUser().getUsername())
			.nickname(owner.getNickname())
			.email(owner.getEmail())
			.phoneNumber(owner.getPhoneNumber())
			.createdAt(owner.getCreatedAt())
			.build();
	}
}
