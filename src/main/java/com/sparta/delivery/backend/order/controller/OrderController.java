package com.sparta.delivery.backend.order.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.order.dto.ReqOrderCreateDto;
import com.sparta.delivery.backend.order.dto.ResOrderCreateDto;
import com.sparta.delivery.backend.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/stores/{storeId}/orders")
	public ResOrderCreateDto createOrder(@PathVariable UUID storeId,
		@RequestBody ReqOrderCreateDto reqOrderCreateDto) {
		return orderService.createOrder(storeId, reqOrderCreateDto);
	}
}
