package com.sparta.delivery.backend.store.dto;

import java.time.Instant;

import com.sparta.delivery.backend.store.entity.StoreStatusEnum;

import lombok.Getter;

@Getter
public class StoreSearchCondition {
	private String keyword;
	private double reviewRate;
	private int reviewCnt;
	private StoreStatusEnum status;
	private Integer minOrderPrice;
	private int deliveryFee;

	private String sortBy = "createdAt";
	private boolean isAsc = true;
	private int size = 10;
}
