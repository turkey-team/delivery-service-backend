package com.sparta.delivery.backend.category.dto;

import java.util.UUID;

import com.sparta.delivery.backend.category.entity.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ResGetListCategoryDto", description = "카테고리 목록 조회 응답 DTO")
public class ResGetListCategoryDto {
	@Schema(description = "카테고리 ID", example = "123e4567-e89b-12d3-a456-426614174000")
	private UUID categoryId;
	@Schema(description = "카테고리명", example = "한식")
	private String categoryName;

	public ResGetListCategoryDto(Category category) {
		this.categoryId = category.getId();
		this.categoryName = category.getName();
	}
}
