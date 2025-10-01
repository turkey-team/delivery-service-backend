package com.sparta.delivery.backend.store.menu.dto;

import java.time.Instant;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResVisibilityDto {
	private Instant hiddenAt;

	public ResVisibilityDto(StoreMenu storeMenu) {
		this.hiddenAt = storeMenu.getHiddenAt();
	}
}
