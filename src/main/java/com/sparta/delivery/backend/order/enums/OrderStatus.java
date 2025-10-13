package com.sparta.delivery.backend.order.enums;

public enum OrderStatus {
	ORDERED, // 주문 중 (수락, 거절 대기중)
	ACCEPTED, // 주문 완료 (수락)
	CANCELLED, // 주문 취소됨 (매장 측 거절, 본인이 취소)
	COMPLETED // 배달 완료
}
