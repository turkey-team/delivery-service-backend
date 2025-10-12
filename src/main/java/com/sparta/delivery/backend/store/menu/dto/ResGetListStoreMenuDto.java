package com.sparta.delivery.backend.store.menu.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.enums.StockStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResGetListStoreMenuDto {
	private UUID id;
	private String name;
	private String imageUrl;
	private int price;
	private String description;
	private String prepTime;
	private StockStatus stockStatus;
	private int sortOrder;
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
