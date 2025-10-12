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

@JsonInclude(Include.NON_NULL) // Null 값은 JSON에 미포함
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResGetListOrderDto {

	private UUID id;             // 주문 ID
	private UUID storeId;        // 가게 ID
	private UUID customerId;     // 고객 ID
	private UUID dongId;         // 동 ID
	private String gu;           // 구 이름
	private String dong;         // 동 이름
	private String addressDetail;// 상세 주소
	private String orderStatus;  // ORDERED, ACCEPTED, CANCELLED, COMPLETED
	private Instant createdAt;   // 주문 생성일
	private int totalPrice;      // 총 가격

	// "CANCELLED" 상태일 때만 화면에 표기
	private Instant cancelledAt;
	private UUID cancelledBy;
	private String cancelledReason;

	public static ResGetListOrderDto from(Order order, int totalPrice) {
		return ResGetListOrderDto.builder()
			.id(order.getId())
			.storeId(order.getStore().getId())
			.customerId(order.getCustomer().getId())
			.dongId(order.getDongEntity().getId())
			.gu(order.getGu())
			.dong(order.getDong())
			.addressDetail(order.getAddressDetails())
			.orderStatus(order.getOrderStatus().name())
			.createdAt(order.getCreatedAt())
			.totalPrice(totalPrice)
			.cancelledAt(order.getCancelledAt())
			.cancelledBy(order.getCancelledBy() != null ? UUID.fromString(order.getCancelledBy().toString()) : null)
			.cancelledReason(order.getCancelledReason())
			.build();
	}
}
