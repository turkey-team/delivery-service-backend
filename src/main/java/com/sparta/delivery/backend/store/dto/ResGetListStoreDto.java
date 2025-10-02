package com.sparta.delivery.backend.store.dto;

import java.util.UUID;

import com.sparta.delivery.backend.store.entity.StoreStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResGetListStoreDto {
	private UUID storeId;
	private String Name;
	private int reviewCnt;
	private double reviewRate;
	private int deliveryFee;
	private Integer minOrderPrice;
	private StoreStatusEnum status;

	@Builder
	public ResGetListStoreDto(UUID storeId, String storeName, double reviewRate, int reviewCnt, int deliveryFee, Integer minOrderPrice, StoreStatusEnum status){
		this.storeId = storeId;
		this.Name = storeName;
		this.reviewCnt = reviewCnt;
		this.reviewRate = reviewRate;
		this.deliveryFee = deliveryFee;
		this.minOrderPrice = minOrderPrice;
		this.status = status;
	}
}
