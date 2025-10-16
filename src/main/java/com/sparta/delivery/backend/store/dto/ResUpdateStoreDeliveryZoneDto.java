package com.sparta.delivery.backend.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ResUpdateStoreDeliveryZoneDto", description = "가게 배달가능지역 응답 DTO")
public class ResUpdateStoreDeliveryZoneDto {

	@Schema(description = "배달 가능 지역(법정동 이름)", example = "114", requiredMode = Schema.RequiredMode.REQUIRED)
	private String deliveryRegionName;

	public ResUpdateStoreDeliveryZoneDto(String deliveryRegionName) {
		this.deliveryRegionName = deliveryRegionName;
	}

	public static ResUpdateStoreDeliveryZoneDto from(String deliveryRegionName) {
		return new ResUpdateStoreDeliveryZoneDto(deliveryRegionName);
	}

}
