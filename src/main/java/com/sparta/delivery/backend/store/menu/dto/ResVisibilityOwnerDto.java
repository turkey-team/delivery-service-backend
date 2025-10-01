package com.sparta.delivery.backend.store.menu.dto;

import java.time.Instant;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResVisibilityOwnerDto {
	private Instant hiddenAt;

	public ResVisibilityOwnerDto(StoreMenu storeMenu) {
		this.hiddenAt = storeMenu.getHiddenAt();
	}
}
