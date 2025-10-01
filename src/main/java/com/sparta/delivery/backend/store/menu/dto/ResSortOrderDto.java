package com.sparta.delivery.backend.store.menu.dto;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResSortOrderDto {
	private int sortOrder;

	public ResSortOrderDto(StoreMenu storeMenu) {
		this.sortOrder = storeMenu.getSortOrder();
	}
}