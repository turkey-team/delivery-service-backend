package com.sparta.delivery.backend.store.dto;

import java.util.List;
import java.util.UUID;

import com.sparta.delivery.backend.store.entity.StoreStatusEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class StoreResponseDto {
	private UUID id;
	private String name;
	private String addressDetails;
	private List<String> images;
	private double reviewRate;
	private StoreStatusEnum status;
	private Integer minOrderPrice;
	private int deliveryFee;
	private String phoneNumber;
}
