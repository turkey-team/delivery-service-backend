package com.sparta.delivery.backend.cart.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ResDeleteCartsDto", description = "장바구니 전체 삭제 응답 DTO")
public class ResDeleteCartsDto {
	@Schema(example = "123e4567-e89b-12d3-a456-426614174000", description = "장바구니를 가진 Customer UUID")
	private UUID customerId;
	@Schema(example = "김밥천국", description = "삭제된 장바구니의 가게 이름")
	private String storeName;
}
