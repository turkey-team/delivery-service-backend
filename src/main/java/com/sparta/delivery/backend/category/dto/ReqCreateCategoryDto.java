package com.sparta.delivery.backend.category.dto;

import com.sparta.delivery.backend.category.repository.CategoryRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqCreateCategoryDto {
	@NotBlank(message = "카테고리 이름은 필수입니다.")
	private String name;

	public String getName() {
		return name;
	}
}
