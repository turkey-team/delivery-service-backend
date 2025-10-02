package com.sparta.delivery.backend.category.dto;

import java.time.Instant;

import com.sparta.delivery.backend.category.entity.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResUpdateCategoryDto {
	private String name;
	private Instant UpdatedAt;
	private Long UpdatedBy;

	public ResUpdateCategoryDto(Category category) {
		this.name = category.getName();
		this.UpdatedAt = category.getUpdatedAt();
		this.UpdatedBy = category.getUpdatedBy();
	}
}
