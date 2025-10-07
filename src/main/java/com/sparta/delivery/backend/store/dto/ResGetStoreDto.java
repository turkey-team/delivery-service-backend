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
public class ResGetStoreDto {
	private UUID storeid;
	private String name;
	private double reviewRate;
	private int reviewCnt;
	private Integer minOrderPrice;
	private int deliveryFee;
	private StoreStatusEnum status;
	private String imageUrl;

	private String description;
	private String holiday;
	private String operationHours;


	@Builder
	public ResGetStoreDto(UUID storeid, String name, double reviewRate, int reviewCnt, StoreStatusEnum status, String description, String holiday, String operationHours, String imageUrl, int deliveryFee, Integer minOrderPrice) {
		this.storeid = storeid;
		this.name = name;
		this.reviewRate = reviewRate;
		this.reviewCnt = reviewCnt;
		this.imageUrl = imageUrl;
		this.status = status;
		this.description = description;
		this.holiday = holiday;
		this.operationHours = operationHours;
		this.deliveryFee = deliveryFee;
		this.minOrderPrice = minOrderPrice;
	}
}
