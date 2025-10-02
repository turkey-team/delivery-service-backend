package com.sparta.delivery.backend.store.menu.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResUpdateVisibilityDto {
	private UUID menuId;
	private Instant hiddenAt;

	public ResUpdateVisibilityDto(StoreMenu storeMenu) {
		this.menuId = storeMenu.getId();
		this.hiddenAt = storeMenu.getHiddenAt();
	}
}
