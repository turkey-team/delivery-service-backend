package com.sparta.delivery.backend.category.dto;

import java.util.UUID;

import com.sparta.delivery.backend.category.entity.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResGetListCategoryDto {
	private UUID categoryId;
	private String categoryName;

	public ResGetListCategoryDto(Category category) {
		this.categoryId = category.getId();
		this.categoryName = category.getName();
	}
}
