package com.sparta.delivery.backend.store.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqUpdateSortOrderDto {
	@Schema(description = "메뉴 순서", example = "1")
	private int sortOrder;

	public ReqUpdateSortOrderDto(int sortOrder) {
		this.sortOrder = sortOrder;
	}
}
