package com.sparta.delivery.backend.store.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreRequestDto {
	private String name;
	private String resionDong;
	private String resionCode;
	private String addressDetail;
	private String phoneNumber;
	private int deliveryFee;
	private Integer minOrderPrice;
	private String status;
	private UUID categoryId;
	private Map<String, List<String>> images;
	// store : 가게
	// business : 사업자등록증

	//details
	private String description;
	private String operatingHours;
	private String holiday;

}
