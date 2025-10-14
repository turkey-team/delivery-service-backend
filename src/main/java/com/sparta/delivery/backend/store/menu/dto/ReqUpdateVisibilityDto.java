package com.sparta.delivery.backend.store.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqUpdateVisibilityDto {

	@Schema(description = "숨기기", example = "true")
	private boolean isHidden;

	public ReqUpdateVisibilityDto(boolean isHidden) {
		this.isHidden = isHidden;
	}
}
