package com.sparta.delivery.backend.order.dto;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sparta.delivery.backend.order.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_NULL) // Null 값은 JSON 에 미포함
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResGetOrderDto {

	private String orderId;          // 주문 ID
	private String orderStatus;      // ORDERED, ACCEPTED, CANCELLED, COMPLETED
	private String storeName;        // 가게 이름
	private String storeImageUrl;    // 가게 이미지 URL
	private String sidoName;         // 시/도
	private String sigunguName;      // 시군구
	private String dongName;         // 동
	private String addressDetail;    // 상세 주소
	private String phoneNumber;      // 연락처
	private Instant createdAt;       // 주문 생성일
	private int totalPrice;          // 총 결제 금액

	// "CANCELLED" 상태일 때만 화면에 표기
	private Instant cancelledAt;     // 취소일, 취소되지 않으면 null
	private UUID cancelledBy;    // 취소한 사람 ID, 취소되지 않으면 null
	private String cancelledReason;  // 취소 사유, 취소되지 않으면 null

	public static ResGetOrderDto from(Order order, int totalPrice) {
		return ResGetOrderDto.builder()
			.orderId(order.getId().toString())
			.orderStatus(order.getOrderStatus().name())
			.storeName(order.getStore().getName())
			.storeImageUrl(order.getStore().getStoreImages().toString())
			.sidoName(order.getGu())
			.sigunguName(order.getDong())
			.dongName(order.getDongEntity().getName())
			.addressDetail(order.getAddressDetails())
			.phoneNumber(order.getStore().getPhoneNumber())
			.createdAt(order.getCreatedAt())
			.totalPrice(totalPrice)
			.cancelledAt(order.getCancelledAt())
			.cancelledBy(order.getCancelledBy() != null ? UUID.fromString(order.getCancelledBy().toString()) : null)
			.cancelledReason(order.getCancelledReason())
			.build();
	}
}
