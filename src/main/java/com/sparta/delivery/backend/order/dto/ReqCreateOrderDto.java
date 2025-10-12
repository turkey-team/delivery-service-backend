package com.sparta.delivery.backend.order.dto;

import com.sparta.delivery.backend.payment.entity.PayMethod;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqCreateOrderDto {

	// 고객 주문 시 요청 메시지
	private String requestMessage;

	// 결제 수단: 현재는 CARD 고정
	private PayMethod payMethod;

}
