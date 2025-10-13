package com.sparta.delivery.backend.category.dto;

import com.sparta.delivery.backend.category.repository.CategoryRepository;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ReqCreateCategoryDto", description = "카테고리 생성 요청 DTO")
public class ReqCreateCategoryDto {
	@NotBlank(message = "카테고리 이름은 필수입니다.")
	@Schema(description = "생성할 카테고리명", example = "야식", required = true)
	private String name;

	public String getName() {
		return name;
	}
}
