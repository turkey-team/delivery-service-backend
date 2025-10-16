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

@JsonInclude(Include.NON_NULL) // Null 값은 JSON 에 미포함
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResGetOrderDto {
	
	private UUID id;             // 주문 ID

	@Schema(description = "주문 상태", example = "ACCEPT")
	private String orderStatus;
	
	@Schema(description = "가게 이름", example = "맛나 밥집")
	private String storeName;

	@Schema(description = "가게 이미지 URL", example = "https://example.com/image.jpg")
	private String storeImageUrl;

	@Schema(description = "시/도 이름", example = "화정동")
	private String sidoName;

	@Schema(description = "시/군/구 이름", example = "화정동")
	private String sigunguName;

	@Schema(description = "동 이름", example = "화정동")
	private String dongName;

	@Schema(description = "상세 주소", example = "백양로 65")
	private String addressDetail;

	@Schema(description = "고객 연락처", example = "01012345678")
	private String phoneNumber;

	@Schema(description = "주문 생성일", example = "2025-10-13T06:43:02.892Z")
	private Instant createdAt;

	@Schema(description = "총 가격", example = "15000")
	private int totalPrice;

	// "CANCELLED" 상태일 때만 화면에 표기
	@Schema(description = "취소일", example = "2025-10-13T06:43:02.892Z")
	private Instant cancelledAt;
	@Schema(description = "취소한 사람", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
	private UUID cancelledBy;
	@Schema(description = "취소 사유", example = "밥이 떨어졌어요")
	private String cancelledReason;

	public static ResGetOrderDto from(Order order, int totalPrice) {
		return ResGetOrderDto.builder()
			.id(order.getId())
			.orderStatus(order.getOrderStatus().name())
			.storeName(order.getStore().getName())
			.storeImageUrl(order.getStore().getStoreImages().toString())
			.sidoName(order.getGu())
			.sigunguName(order.getDong())
			.dongName(order.getDongEntity().getName())
			.addressDetail(order.getAddressDetails())
			.phoneNumber(order.getStore().getFormattedPhoneNumber())
			.createdAt(order.getCreatedAt())
			.totalPrice(totalPrice)
			.cancelledAt(order.getCancelledAt())
			.cancelledBy(order.getCancelledBy() != null ? UUID.fromString(order.getCancelledBy().toString()) : null)
			.cancelledReason(order.getCancelledReason())
			.build();
	}
}
