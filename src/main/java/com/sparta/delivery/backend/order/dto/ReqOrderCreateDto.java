package com.sparta.delivery.backend.order.dto;

import java.util.List;
import java.util.UUID;

import com.sparta.delivery.backend.order.enums.OrderStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqOrderCreateDto {
	/*
	구
	동
	상세 주소
	주문 상태
	*/
	private UUID addressId;
	private OrderStatus orderStatus; // ORDERING 이 default 값

	private List<OrderMenuInfo> orderMenus; // 메뉴 정보

	@Getter
	@NoArgsConstructor
	public static class OrderMenuInfo {
		private UUID menuId;
		private int quantity;
	}
}
