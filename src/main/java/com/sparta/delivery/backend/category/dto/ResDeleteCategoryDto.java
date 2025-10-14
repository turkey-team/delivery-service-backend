package com.sparta.delivery.backend.category.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ResDeleteCategoryDto", description = "카테고리 삭제 응답 DTO")
public class ResDeleteCategoryDto {
	@Schema(description = "삭제된 카테고리 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
	private UUID categoryId;
	@Schema(description = "삭제된 카테고리명", example = "야식")
	private String categoryName;

	@Builder
	public  ResDeleteCategoryDto(UUID categoryId, String categoryName){
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}
}
