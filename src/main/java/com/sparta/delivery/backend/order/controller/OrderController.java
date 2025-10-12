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
import com.sparta.delivery.backend.order.dto.ResCheckOutOrderDto;
import com.sparta.delivery.backend.order.dto.ResGetListOrderDto;
import com.sparta.delivery.backend.order.dto.ResGetOrderDto;
import com.sparta.delivery.backend.order.service.OrderService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	// Customer 가 주문 하기 전 Cart 의 정보들을 확인하는 화면
	@GetMapping("/checkout")
	public ResCheckOutOrderDto getCheckoutOrder(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return orderService.getCheckoutOrder(userDetails.getUser());
	}

	// Customer 가 주문 생성 ( Cart 에서 정보들을 가져와서 주문 처리만 하는 Flow )
	@PostMapping
	public ResponseEntity<UUID> createOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody ReqCreateOrderDto reqCreateOrderDto
	) {
		UUID orderId = orderService.createOrder(userDetails.getUser(), reqCreateOrderDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
	}

	// Customer / Owner 가 자신의 주문 내역 전체 조회
	@GetMapping("/me")
	public ResponseEntity<Page<ResGetListOrderDto>> getOrdersByUserId(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam("page") int page,
		@RequestParam("size") int size
		/*
		@RequestParam(required = false) String address,       // 주소 필터
		@RequestParam(required = false) String storeName,     // 가게 명 필터
		@RequestParam(required = false) String status,        // 주문 상태 필터
		@RequestParam(required = false) String startDate,     // 조회 시작일
		@RequestParam(required = false) String endDate        // 조회 종료일
		*/
	) {
		Page<ResGetListOrderDto> orders = orderService.getOrdersByUser(userDetails.getUser(), page - 1, size);
		return ResponseEntity.ok(orders);
	}

	// 주문 상세 정보 조회
	@GetMapping("/{orderId}")
	public ResponseEntity<ResGetOrderDto> getOrderById(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID orderId
	) {
		ResGetOrderDto order = orderService.getOrderById(userDetails.getUser(), orderId);
		return ResponseEntity.ok(order);
	}

	// Owner 가 {주문 수락 -> 배달 소요 시간 응답 or 주문 취소 (5분 이내) -> 취소 사유 전달}
	@PatchMapping("/{orderId}/status")
	public ResponseEntity<Void> updateOrderStatusDto(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID orderId,
		@RequestBody ReqUpdateOrderStatusDto reqUpdateOrderStatusDto
	) {
		orderService.updateOrderStatus(userDetails.getUser(), orderId, reqUpdateOrderStatusDto);
		return ResponseEntity.ok().build();
	}

	// Customer 가 주문 내역 삭제
	@DeleteMapping("/{orderId}")
	public ResponseEntity<Void> deleteOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID orderId
	) {
		orderService.deleteOrder(userDetails.getUser(), orderId);
		return ResponseEntity.noContent().build();
	}

	/* 주문 수정은 불가: 취소하고 재주문해야함 */
}
