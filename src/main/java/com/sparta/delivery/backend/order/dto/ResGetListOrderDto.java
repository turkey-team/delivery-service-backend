package com.sparta.delivery.backend.order.dto;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sparta.delivery.backend.order.entity.Order;

import io.swagger.v3.oas.annotations.media.Schema;
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

	@Schema(description = "시/군/구 이름", example = "덕양구")
	private String gu;

	@Schema(description = "동 이름", example = "화정동")
	private String dong;

	@Schema(description = "상세 주소", example = "백양로 65")
	private String addressDetail;

	@Schema(description = "주문 상태", example = "COMPLETED")
	private String orderStatus;  // ORDERED, ACCEPTED, CANCELLED, COMPLETED

	@Schema(description = "주문 생성일", example = "2025-10-13T06:43:02.892Z")
	private Instant createdAt;

	@Schema(description = "총 가격", example = "13000")
	private int totalPrice;

	// "CANCELLED" 상태일 때만 화면에 표기
	@Schema(description = "취소일", example = "2025-10-13T06:43:02.892Z")
	private Instant cancelledAt;
	@Schema(description = "취소한 사람", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
	private UUID cancelledBy;
	@Schema(description = "취소 사유", example = "밥이 떨어졌어요")
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
