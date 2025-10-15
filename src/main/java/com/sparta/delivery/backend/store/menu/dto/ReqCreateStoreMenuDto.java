package com.sparta.delivery.backend.store.menu.dto;

import com.sparta.delivery.backend.store.menu.enums.StockStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqCreateStoreMenuDto {
	/*
	이름
	사진
	가격
	설명
	준비 시간
	재고 상태
	숨김 상태
	 */
	@Schema(description = "메뉴 이름", example = "김치찌개")
	private String name;

	@Schema(description = "메뉴 사진 URL", example = "https://example.com/image.jpg")
	private String imageUrl;

	@Schema(description = "가격", example = "8000")
	private int price;

	@Schema(description = "메뉴 설명", example = "집밥 스타일의 맛있는 김치찌개")
	private String description;

	@Schema(description = "준비 시간", example = "15분")
	private String prepTime;

	@Schema(description = "재고 상태", example = "ON_SALE")
	private StockStatus stockStatus;

	@Schema(description = "숨김 여부", example = "false")
	private Boolean isHidden;
}
