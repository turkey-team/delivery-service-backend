package com.sparta.delivery.backend.category.dto;

import java.time.Instant;

import com.sparta.delivery.backend.category.entity.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ResUpdateCategoryDto", description = "카테고리 수정 응답 DTO")
public class ResUpdateCategoryDto {
	@Schema(description = "수정된 카테고리명", example = "아시안")
	private String name;
	@Schema(description = "수정일", example = "2025-10-13T08:23:45.123Z")
	private Instant UpdatedAt;
	@Schema(description = "수정한 User Id", example = "1")
	private Long UpdatedBy;

	public ResUpdateCategoryDto(Category category) {
		this.name = category.getName();
		this.UpdatedAt = category.getUpdatedAt();
		this.UpdatedBy = category.getUpdatedBy();
	}
}
