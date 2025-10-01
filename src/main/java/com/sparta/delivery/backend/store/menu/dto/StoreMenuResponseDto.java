package com.sparta.delivery.backend.store.menu.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.enums.StockStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreMenuResponseDto {
	private UUID id;
	private String name;
	private String imageUrl;
	private int price;
	private String description;
	private String prepTime;
	private StockStatus stockStatus;
	private int sortOrder;
	private Instant hiddenAt;

	public StoreMenuResponseDto(StoreMenu menu) {
		this.id = menu.getId();
		this.name = menu.getName();
		this.price = menu.getPrice();
		this.description = menu.getDescription();
		this.prepTime = menu.getPrepTime();
		this.sortOrder = menu.getSortOrder();
		this.stockStatus = menu.getStockStatus();
		this.hiddenAt = menu.getHiddenAt();
		this.imageUrl = menu.getImage() != null ? menu.getImage().getImageUrl() : null;
	}
}
