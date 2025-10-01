package com.sparta.delivery.backend.store.menu.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.enums.StockStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResStoreMenuCreateDto {
	private UUID id;
	private String name;
	private String imageUrl;
	private int price;
	private String description;
	private String prepTime;
	private StockStatus stockStatus;
	private int sortOrder;
	private Instant hiddenAt;

	public ResStoreMenuCreateDto(StoreMenu storeMenu) {
		this.id = storeMenu.getId();
		this.name = storeMenu.getName();
		this.price = storeMenu.getPrice();
		this.description = storeMenu.getDescription();
		this.prepTime = storeMenu.getPrepTime();
		this.sortOrder = storeMenu.getSortOrder();
		this.stockStatus = storeMenu.getStockStatus();
		this.hiddenAt = storeMenu.getHiddenAt();
		this.imageUrl = storeMenu.getImageUrl();
	}
}
