package com.sparta.delivery.backend.store.menu.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.enums.StockStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResGetStoreMenuDto {
	private UUID id;
	private String name;
	private String imageUrl;
	private int price;
	private String description;
	private String prepTime;
	private StockStatus stockStatus;
	private Instant hiddenAt;

	public ResGetStoreMenuDto(StoreMenu storeMenu) {
		this.id = storeMenu.getId();
		this.name = storeMenu.getName();
		this.imageUrl = storeMenu.getImage() != null ? storeMenu.getImage().getImageUrl() : null;
		this.price = storeMenu.getPrice();
		this.description = storeMenu.getDescription();
		this.prepTime = storeMenu.getPrepTime();
		this.stockStatus = storeMenu.getStockStatus();
		this.hiddenAt = storeMenu.getHiddenAt();
	}
}
