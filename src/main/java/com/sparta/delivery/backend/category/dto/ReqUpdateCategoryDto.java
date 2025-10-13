package com.sparta.delivery.backend.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ReqUpdateCategoryDto", description = "카테고리 수정 요청 DTO")
public class ReqUpdateCategoryDto {
	@NotBlank(message = "변경할 카테고리 이름을 입력해주세요.")
	@Schema(description = "변경할 카테고리명", example = "아시안", required = true)
	private String name;

	public String getName() {
		return name;
	}
}
