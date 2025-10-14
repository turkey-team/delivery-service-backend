package com.sparta.delivery.backend.category.dto;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ResGetCategoryDto", description = "카테고리 상세 조회 응답 DTO")
public class ResGetCategoryDto {
	@Schema(description = "카테고리 Id", example = "123e4567-e89b-12d3-a456-426614174000")
	private UUID categoryId;
	@Schema(description = "카테고리명", example = "한식")
	private String categoryName;
	@Schema(description = "생성일", example = "2025-10-13T08:23:45.123Z")
	private Instant createdAt;
	@Schema(description = "수정일", example = "2025-10-13T08:23:45.123Z")
	private Instant updatedAt;

	@Builder
	public ResGetCategoryDto(UUID id, String categoryName, Instant createdAt, Instant updatedAt) {
		this.categoryId = id;
		this.categoryName = categoryName;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
