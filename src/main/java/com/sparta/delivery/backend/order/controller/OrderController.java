package com.sparta.delivery.backend.order.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.order.dto.ReqCreateOrderDto;
import com.sparta.delivery.backend.order.dto.ReqUpdateOrderStatusDto;
import com.sparta.delivery.backend.order.dto.ResGetListOrderDto;
import com.sparta.delivery.backend.order.dto.ResGetOrderDto;
import com.sparta.delivery.backend.order.dto.ResUpdateOrderStatusDto;
import com.sparta.delivery.backend.order.service.OrderService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	// TODO: Customer 가 주문 생성 ( Cart 에서 정보들을 가져와서 주문 처리만 하는 Flow )
	@PostMapping("/stores/{storeId}/orders")
	public ResponseEntity<Void> createOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID storeId,
		@RequestBody ReqCreateOrderDto reqCreateOrderDto
	) {
		orderService.createOrder(userDetails.getUser(), storeId, reqCreateOrderDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// TODO: User 가 자신의 주문 내역 전체 조회
	@GetMapping("/orders/me")
	public ResponseEntity<Page<ResGetListOrderDto>> getOrdersByUserId(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam("page") int page,
		@RequestParam("size") int size
	) {
		Page<ResGetListOrderDto> orders = orderService.getOrdersByUserId(userDetails.getUser(), page - 1, size);
		return ResponseEntity.ok(orders);
	}

	// TODO: 주문 상세 정보 조회
	@GetMapping("/stores/{storeId}/orders/{orderId}")
	public ResponseEntity<ResGetOrderDto> getOrderByStoreIdAndOrderId(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID storeId,
		@PathVariable UUID orderId
	) {
		ResGetOrderDto order = orderService.getOrderByStoreIdAndOrderId(userDetails.getUser(), storeId, orderId);
		return ResponseEntity.ok(order);
	}

	// TODO: Owner 가 {주문 수락 -> 배달 소요 시간 응답 or 주문 취소 (5분 이내) -> 취소 사유 전달}
	@PatchMapping("/stores/{storeId}/orders/{orderId}/status")
	public ResponseEntity<ResUpdateOrderStatusDto> updateOrderStatusDto(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID storeId,
		@PathVariable UUID orderId,
		@RequestBody ReqUpdateOrderStatusDto reqUpdateOrderStatusDto
	) {
		orderService.updateOrderStatus(userDetails.getUser(), storeId, orderId, reqUpdateOrderStatusDto);
		return ResponseEntity.ok().build();
	}

	// TODO: Customer 가 주문 내역 삭제
	@DeleteMapping("/stores/{storeId}/orders/{orderId}")
	public ResponseEntity<Void> deleteOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID storeId,
		@PathVariable UUID orderId
	) {
		orderService.deleteOrder(userDetails.getUser(), storeId, orderId);
		return ResponseEntity.noContent().build();
	}

	/* 주문 수정은 불가: 취소하고 재주문해야함 */
}
