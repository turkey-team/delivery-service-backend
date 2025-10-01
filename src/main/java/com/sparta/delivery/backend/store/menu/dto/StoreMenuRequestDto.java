package com.sparta.delivery.backend.store.menu.dto;

import java.time.Instant;

import com.sparta.delivery.backend.store.menu.enums.StockStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreMenuRequestDto {
	/*
	이름
	사진
	가격
	설명
	준비 시간
	재고 상태
	메뉴 순서
	숨김 상태
	 */
	private String name;
	private String imageUrl;
	private int price;
	private String description;
	private String prepTime;
	private StockStatus stockStatus;
	private int sortOrder;
	private Instant hiddenAt;
}
