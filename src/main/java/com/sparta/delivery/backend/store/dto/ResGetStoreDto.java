package com.sparta.delivery.backend.store.dto;

import java.util.UUID;

import com.sparta.delivery.backend.store.entity.StoreStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ResGetStoreDto", description = "가게 상세조회 응답 DTO")
public class ResGetStoreDto {
	@Schema(description = "가게 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
	private UUID storeId;
	@Schema(description = "가게명", example = "김밥천국")
	private String name;
	@Schema(description = "리뷰평균점수", example = "4.5")
	private double reviewRate;
	@Schema(description = "리뷰개수", example = "500")
	private int reviewCnt;
	@Schema(description = "최소주문금액", example = "12000")
	private Integer minOrderPrice;
	@Schema(description = "배달료", example = "5000")
	private int deliveryFee;
	@Schema(description = "가게 상태", example = "OPEN")
	private StoreStatusEnum status;
	@Schema(description = "가게사진", example = "image.png")
	private String imageUrl;
	@Schema(description = "가게 설명", example = "광화문 김밥천국입니다.")
	private String description;
	@Schema(description = "가게 휴업일", example = "일요일")
	private String holiday;
	@Schema(description = "가게 운영시간", example = "08:00 ~ 20:00")
	private String operationHours;
	@Schema(description = "가게 전화번호", example = "02-352-3333")
	private String phoneNumber;

	@Builder
	public ResGetStoreDto(UUID storeId, String name, double reviewRate, int reviewCnt, StoreStatusEnum status, String description, String holiday, String operationHours, String imageUrl, int deliveryFee, Integer minOrderPrice, String phoneNumber) {
		this.storeId = storeId;
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
		this.phoneNumber = phoneNumber;
	}
}
