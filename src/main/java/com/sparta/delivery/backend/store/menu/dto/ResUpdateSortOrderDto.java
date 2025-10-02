package com.sparta.delivery.backend.store.menu.dto;

import java.util.UUID;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResUpdateSortOrderDto {
	private UUID menuId;
	private int sortOrder;

	public ResUpdateSortOrderDto(StoreMenu storeMenu) {
		this.menuId = storeMenu.getId();
		this.sortOrder = storeMenu.getSortOrder();
	}
}