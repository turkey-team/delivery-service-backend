package com.sparta.delivery.backend.store.menu.dto;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResSortOrderOwnerDto {
	private int sortOrder;

	public ResSortOrderOwnerDto(StoreMenu storeMenu) {
		this.sortOrder = storeMenu.getSortOrder();
	}
}
