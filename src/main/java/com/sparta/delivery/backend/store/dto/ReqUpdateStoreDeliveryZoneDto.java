package com.sparta.delivery.backend.store.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ReqUpdateStoreDeliveryZoneDto", description = "가게 배달가능지역 수정 요청 DTO")
public class ReqUpdateStoreDeliveryZoneDto {

	@NotEmpty(message = "배달 가능 지역을 최소 1개 이상 선택해주세요.")
	@Schema(description = "배달 가능 지역(법정동) 리스트", example = "[114, 115]", requiredMode = Schema.RequiredMode.REQUIRED)
	private List<String> deliveryRegions;

}
