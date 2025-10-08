package com.sparta.delivery.backend.cart.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResDeleteCartsDto {
	private UUID customerId;
	private String storeName;
}
