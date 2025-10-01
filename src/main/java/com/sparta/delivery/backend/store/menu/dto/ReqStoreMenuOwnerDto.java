package com.sparta.delivery.backend.store.menu.dto;

import java.time.Instant;

import com.sparta.delivery.backend.store.menu.enums.StockStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqStoreMenuOwnerDto {
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
	private Boolean hiddenAt; // 체크박스로 정보가 넘어오기때문에 true false 를 instant 로 변환처리
}
