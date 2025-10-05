package com.sparta.delivery.backend.order.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	// TODO: Customer 가 주문 생성 ( Cart 에서 정보들을 가져와서 주문 처리만 하는 Flow )

	// TODO: Customer 가 주문 조회

	// TODO: Owner 가 주문 수락 -> 배달 소요 시간 응답

	// TODO: Owner 또는 Master 가 주문 거절 -> 취소 사유 응답

	// TODO: Customer 가 주문 취소 (5분 이내) -> 취소 사유 전달

	// TODO: Customer 가 주문 내역 삭제

	// 주문 수정은 불가
}
