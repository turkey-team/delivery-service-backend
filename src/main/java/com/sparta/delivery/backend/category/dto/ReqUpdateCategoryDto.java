package com.sparta.delivery.backend.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqUpdateCategoryDto {
	@NotBlank(message = "변경할 카테고리 이름을 입력해주세요.")
	private String name;

	public String getName() {
		return name;
	}
}
