package com.sparta.delivery.backend.store.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResUpdateStoreDetailsDto {
	private UUID storeId;
	private String storeName;
	private Integer minOrderPrice;
	private int deliveryFee;

	private String holiday;
	private String operationHours;
	private String description;

	@Builder
	public ResUpdateStoreDetailsDto(UUID storeId
		, String storeName
		, Integer minOrderPrice
		, int deliveryFee
		, String operationHours
		, String holiday
		, String description) {
		this.storeId = storeId;
		this.storeName = storeName;
		this.minOrderPrice = minOrderPrice;
		this.deliveryFee = deliveryFee;
		this.operationHours = operationHours;
		this.description = description;
		this.holiday = holiday;
	}
}
