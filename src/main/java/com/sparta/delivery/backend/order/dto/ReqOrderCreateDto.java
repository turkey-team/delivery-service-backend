package com.sparta.delivery.backend.order.dto;

import com.sparta.delivery.backend.order.enums.OrderStatus;

public class ReqOrderCreateDto {
	/*
	구
	동
	상세 주소
	주문 상태
	*/
	private String gu;
	private String dong;
	private String addressDetails;
	private OrderStatus orderStatus; // 유효하지 않은 값이 들어오면 에러 처리가 불가능하기에 프론트 단에서 올바른 값만 넘어오도록 해야함
}
