package com.sparta.delivery.backend.store.menu.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.enums.StockStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResGetListStoreMenuDto {
	private UUID id;

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

	@Schema(description = "재고 상태", example = "LOW_STOCK")
	private StockStatus stockStatus;

	@Schema(description = "메뉴 순서", example = "3")
	private int sortOrder;

	@Schema(description = "숨긴 시간", example = "2025-10-13T06:13:17.036Z")
	private Instant hiddenAt;

	public ResGetListStoreMenuDto(StoreMenu storeMenu) {
		this.id = storeMenu.getId();
		this.name = storeMenu.getName();
		this.imageUrl = storeMenu.getImage() != null ? storeMenu.getImage().getImageUrl() : null;
		this.price = storeMenu.getPrice();
		this.description = storeMenu.getDescription();
		this.prepTime = storeMenu.getPrepTime();
		this.stockStatus = storeMenu.getStockStatus();
		this.sortOrder = storeMenu.getSortOrder();
		this.hiddenAt = storeMenu.getHiddenAt();
	}
}
