package com.sparta.delivery.backend.store.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResDeleteStoreDto {
	private UUID storeId;
	private String storeName;
	private String businessNumber;

	@Builder
	public ResDeleteStoreDto(UUID storeId, String storeName, String businessNumber) {
		this.storeId = storeId;
		this.storeName = storeName;
		this.businessNumber = businessNumber;
	}
}
