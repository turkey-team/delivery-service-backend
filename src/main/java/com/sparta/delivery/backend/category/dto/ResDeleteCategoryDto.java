package com.sparta.delivery.backend.category.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResDeleteCategoryDto {
	private UUID categoryId;
	private String categoryName;

	@Builder
	public  ResDeleteCategoryDto(UUID categoryId, String categoryName){
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}
}
