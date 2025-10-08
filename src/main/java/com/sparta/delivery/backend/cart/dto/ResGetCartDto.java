package com.sparta.delivery.backend.cart.dto;

import java.util.List;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResGetCartDto {
	// Store
	private String storeName;
	private Integer minOrderPrice;
	private int deliveryFee;
	private List<CartDto> cartDtoList;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CartDto {
		private UUID cartId;
		private String menuName;
		private int price;
		private int quantity;
		private String imgUrl;
	}
}
