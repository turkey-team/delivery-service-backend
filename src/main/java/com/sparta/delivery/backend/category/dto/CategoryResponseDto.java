package com.sparta.delivery.backend.category.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.category.entity.Category;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryResponseDto {

	private UUID id;
	private String name;
	private Instant createdAt;
	private Instant updatedAt;
	private Instant deletedAt;
	private Long createdBy;
	private Long updatedBy;
	private Long deletedBy;

	public CategoryResponseDto(Category category) {
		this.id = category.getId();
		this.name = category.getName();
		this.createdAt = category.getCreatedAt();
		this.updatedAt = category.getUpdatedAt();
		this.deletedAt = category.getDeletedAt();
		this.createdBy = category.getCreatedBy();
		this.updatedBy = category.getUpdatedBy();
		this.deletedBy = category.getDeletedBy();
	}
}
