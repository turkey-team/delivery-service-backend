package com.sparta.delivery.backend.store.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ResUpdateStoreDetailsDto", description = "가게 배달 관련 수정 응답 DTO")
public class ResUpdateStoreDetailsDto {
	@Schema(description = "가게 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
	private UUID storeId;
	@Schema(description = "가게 이름", example = "김밥천국")
	private String storeName;
	@Schema(description = "최소주문금액", example = "12000")
	private Integer minOrderPrice;
	@Schema(description = "배달료", example = "1500")
	private int deliveryFee;

	@Schema(description = "휴업일", example = "일요일")
	private String holiday;
	@Schema(description = "영업시간", example = "10:00~22:00")
	private String operationHours;
	@Schema(description = "가게 소개", example = "김밥천국 광화문점입니다.")
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
