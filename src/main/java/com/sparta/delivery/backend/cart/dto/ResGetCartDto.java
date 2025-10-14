package com.sparta.delivery.backend.cart.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ResGetCartDto",description = "장바구니 조회 응답 DTO")
public class ResGetCartDto {
	// Store
	@Schema(description = "수정할 이름", example = "김밥천국 1호점", required = true)
	private String storeName;
	@Schema(description = "최소주문금액", example = "12000")
	private Integer minOrderPrice;
	@Schema(description = "배달료", example = "1500")
	private int deliveryFee;
	@Schema(name = "cartDtoList",description = "장바구니 메뉴")
	private List<CartDto> cartDtoList;

	@Getter
	@NoArgsConstructor
	public static class CartDto {
		@Schema(description = "장바구니 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
		private UUID cartId;
		@Schema(description = "메뉴 이름", example = "치즈돈가스")
		private String menuName;
		@Schema(description = "메뉴 가격", example = "10000")
		private int price;
		@Schema(description = "메뉴 개수", example = "1")
		private Long quantity;
		@Schema(description = "메뉴 사진 URL", example = "image.png")
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
