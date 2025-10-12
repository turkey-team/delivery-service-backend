package com.sparta.delivery.backend.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.delivery.backend.order.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqUpdateOrderStatusDto {
	private OrderStatus orderStatus;
	private String cancelledReason;
}
