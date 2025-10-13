package com.sparta.delivery.backend.category.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.category.entity.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ResCreateCategoryDto", description = "카테고리 생성 응답 DTO")
public class ResCreateCategoryDto {

	@Schema(description = "생성한 카테고리 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
	private UUID id;
	@Schema(description = "생성한 카테고리명", example = "야식")
	private String name;
	@Schema(description = "생성일", example = "2025-10-13T08:23:45.123Z")
	private Instant createdAt;
	@Schema(description = "생성자 User Id", example = "1")
	private Long createdBy;

	public ResCreateCategoryDto(Category category) {
		this.id = category.getId();
		this.name = category.getName();
		this.createdAt = category.getCreatedAt();
		this.createdBy = category.getCreatedBy();
	}
}
