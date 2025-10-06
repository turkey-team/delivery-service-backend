package com.sparta.delivery.backend.store.dto;

import java.util.UUID;

import com.sparta.delivery.backend.store.entity.StoreStatusEnum;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResUpdateStoreStatusDto {
	private UUID storeId;
	private StoreStatusEnum storeStatus;

	@Builder
	public ResUpdateStoreStatusDto(UUID storeId, StoreStatusEnum storeStatus) {
		this.storeId = storeId;
		this.storeStatus = storeStatus;
	}
}
