package com.sparta.delivery.backend.cart.dto;

import java.util.List;
import java.util.UUID;

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
	public static class CartDto {
		private UUID cartId;
		private String menuName;
		private int price;
		private Long quantity;
		private String imgUrl;

		public CartDto(UUID cartId, String menuName, int menuPrice, Long count, String imageUrl) {
			this.cartId = cartId;
			this.menuName = menuName;
			this.price = menuPrice;
			this.quantity = count;
			this.imgUrl = imageUrl;
		}
	}

}
