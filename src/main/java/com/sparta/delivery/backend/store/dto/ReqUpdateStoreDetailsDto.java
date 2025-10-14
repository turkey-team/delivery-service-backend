package com.sparta.delivery.backend.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ReqUpdateStoreDetailsDto", description = "가게 배달 정보 수정 요청 DTO")
public class ReqUpdateStoreDetailsDto {
	@Schema(description = "최소주문금액", example = "12000")
	private Integer minOrderPrice;
	@Schema(description = "배달료", example = "1500")
	private int deliveryFee;

	@Schema(description = "휴업일", example = "일요일")
	private String holiday;
	@NotBlank(message = "영업시간을 설정해주세요.")
	@Schema(description = "영업시간", example = "10:00~22:00", required = true)
	private String operationHours;
	@Schema(description = "가게 소개", example = "김밥천국 광화문점입니다.")
	private String description;
}
