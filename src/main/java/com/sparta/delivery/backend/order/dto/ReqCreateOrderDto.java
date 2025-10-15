package com.sparta.delivery.backend.order.dto;

import java.util.List;
import java.util.UUID;

import com.sparta.delivery.backend.payment.entity.PayMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqCreateOrderDto {

	// 고객 주문 시 요청 메시지
	@Schema(description = "고객 요청사항", example = "문 앞에 놔주세요")
	private String requestMessage;

	// 결제 수단: 현재는 CARD 고정
	@Schema(description = "결제 수단", example = "CARD")
	private PayMethod payMethod;

	@Schema(description = "주문 메뉴 목록")
	private List<OrderMenuRequest> orderMenus;

	@Getter
	@NoArgsConstructor
	public static class OrderMenuRequest {

		private UUID storeMenuId;

		@Schema(description = "주문 수량", example = "2")
		private int quantity;
	}
}
