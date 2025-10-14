package com.sparta.delivery.backend.cart.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ReqCreateCartDto", description = "장바구니 생성 요청 DTO")
public class ReqCreateCartDto {

	@NotNull
	@Schema(description = "장바구니에 추가할 메뉴 Id", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
	private UUID menuId;

}
