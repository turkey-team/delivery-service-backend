package com.sparta.delivery.backend.category.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.category.entity.Category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResGetCategoryDto {
	private UUID categoryId;
	private String categoryName;
	private Instant createdAt;
	private Instant updatedAt;

	@Builder
	public ResGetCategoryDto(UUID id, String categoryName, Instant createdAt, Instant updatedAt, Instant deletedAt) {
		this.categoryId = id;
		this.categoryName = categoryName;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
