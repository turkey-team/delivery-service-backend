package com.sparta.delivery.backend.store.dto;

import java.util.UUID;

import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name="ResGetListStoreDto", description = "가게 목록 조회 응답 DTO")
public class ResGetListStoreDto {
	@Schema(description = "가게 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
	private UUID storeId;
	@Schema(description = "가게명", example = "김밥천국")
	private String name;
	@Schema(description = "리뷰개수", example = "50")
	private int reviewCnt;
	@Schema(description = "리뷰평균점수", example = "4.5")
	private double reviewRate;
	@Schema(description = "배달료", example = "1500")
	private int deliveryFee;
	@Schema(description = "주문최소금액", example = "12000")
	private Integer minOrderPrice;
	@Schema(description = "가게 상태", example = "OPEN")
	private StoreStatusEnum status;

	@Builder
	public ResGetListStoreDto(UUID storeId, String name, double reviewRate, int reviewCnt, int deliveryFee, Integer minOrderPrice, StoreStatusEnum status){
		this.storeId = storeId;
		this.name = name;
		this.reviewCnt = reviewCnt;
		this.reviewRate = reviewRate;
		this.deliveryFee = deliveryFee;
		this.minOrderPrice = minOrderPrice;
		this.status = status;
	}

	public static ResGetListStoreDto from(Store store){
		return ResGetListStoreDto.builder()
			.storeId(store.getId())
			.name(store.getName())
			.reviewCnt(store.getReviewCnt())
			.reviewRate(store.getReviewRate())
			.deliveryFee(store.getDeliveryFee())
			.minOrderPrice(store.getMinOrderPrice())
			.status(store.getStatus())
			.build();
	}
}
