package com.sparta.delivery.backend.cart.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ResCreateCartDto", description = "장바구니 생성 응답 DTO")
public class ResCreateCartDto {
	@Schema(description = "생성한 장바구니 UUID",example = "123e4567-e89b-12d3-a456-426614174000", required = true)
	private UUID cartId;
	@Schema(description = "추가한 메뉴 이름", example = "짜장면", required = true)
	private String menuName;
}
