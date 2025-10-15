package com.sparta.delivery.backend.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.delivery.backend.order.enums.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqUpdateOrderStatusDto {
	@Schema(description = "주문 상태", example = "ACCEPTED")
	private OrderStatus orderStatus;

	@Schema(description = "취소 사유", example = "가게 마감")
	private String cancelledReason;
}
