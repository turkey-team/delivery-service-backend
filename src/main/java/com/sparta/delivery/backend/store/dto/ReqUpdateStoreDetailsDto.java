package com.sparta.delivery.backend.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqUpdateStoreDetailsDto {
	private Integer minOrderPrice;
	private int deliveryFee;

	private String holiday;
	@NotBlank(message = "영업시간을 설정해주세요.")
	private String operationHours;
	private String description;
}
