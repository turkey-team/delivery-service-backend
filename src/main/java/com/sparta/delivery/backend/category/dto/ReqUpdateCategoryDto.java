package com.sparta.delivery.backend.category.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqUpdateCategoryDto {
	@NotNull(message = "변경할 이름을 입력해주세요.")
	private String name;

	public String getName() {
		return name;
	}
}
