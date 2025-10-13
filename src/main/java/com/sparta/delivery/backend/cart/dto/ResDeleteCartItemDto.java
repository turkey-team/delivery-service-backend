package com.sparta.delivery.backend.cart.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ResDeleteCartItemDto",description = "장바구니 메뉴 삭제 응답 DTO")
public class ResDeleteCartItemDto {
	@Schema(description = "삭제한 cartId", example = "123e4567-e89b-12d3-a456-426614174000")
	private UUID cartId;
	@Schema(description = "삭제한 메뉴 이름", example = "치즈돈가스")
	private String menuName;
}
