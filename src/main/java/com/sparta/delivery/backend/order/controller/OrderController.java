package com.sparta.delivery.backend.order.controller;

import com.sparta.delivery.backend.global.common.dto.PageResponse;
import com.sparta.delivery.backend.order.dto.*;
import com.sparta.delivery.backend.order.service.OrderService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@Tag(name="Order-Controller", description = "주문 관련 API")
public class OrderController {

	private final OrderService orderService;

	// Customer 가 주문 하기 전 Cart 의 정보들을 확인하는 화면
	@GetMapping("/checkout")
	@Operation(summary = "주문 결제 전 화면 조회", description = "Customer가 주문 생성 전에 장바구니 정보, 배송지, 금액을 확인합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "주문 정보 조회 성공", content = @Content(schema = @Schema(implementation = ResCheckOutOrderDto.class))),
		@ApiResponse(responseCode = "400", description = "장바구니가 비어있거나 주소 정보 없음")
	})
	public ResCheckOutOrderDto getCheckoutOrder(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return orderService.getCheckoutOrder(userDetails.getUser());
	}

	// Customer 가 주문 생성 ( Cart 에서 정보들을 가져와서 주문 처리만 하는 Flow )
	@PostMapping
	@Operation(summary = "주문 생성", description = "Customer가 장바구니에 담긴 정보를 기반으로 주문을 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "주문 생성 성공"),
		@ApiResponse(responseCode = "400", description = "주소 정보나 장바구니 정보 없음"),
		@ApiResponse(responseCode = "403", description = "본인 장바구니가 아님")
	})
	public ResponseEntity<UUID> createOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody ReqCreateOrderDto reqCreateOrderDto
	) {
		UUID orderId = orderService.createOrder(userDetails.getUser(), reqCreateOrderDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
	}

	// Customer / Owner 가 자신의 주문 내역 전체 조회
	@GetMapping("/me")
	@Operation(summary = "주문 내역 전체 조회", description = "주문 내역을 페이징 형태로 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "주문 내역 조회 성공", content = @Content(schema = @Schema(implementation = ResGetListOrderDto.class))),
		@ApiResponse(responseCode = "400", description = "유효하지 않은 사용자 정보")
	})
	public ResponseEntity<PageResponse<ResGetListOrderDto>> getOrdersByUserId(
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
		PageResponse<ResGetListOrderDto> orders = orderService.getOrdersByUser(userDetails.getUser(), page - 1, size);
		return ResponseEntity.ok(orders);
	}

	// 주문 상세 정보 조회
	@GetMapping("/{orderId}")
	@Operation(summary = "주문 내역 상세 조회", description = "자신의 주문 내역을 페이징 형태로 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "주문 내역 조회 성공", content = @Content(schema = @Schema(implementation = ResGetListOrderDto.class))),
		@ApiResponse(responseCode = "400", description = "유효하지 않은 사용자 정보")
	})
	public ResponseEntity<ResGetOrderDto> getOrderById(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID orderId
	) {
		ResGetOrderDto order = orderService.getOrderById(userDetails.getUser(), orderId);
		return ResponseEntity.ok(order);
	}

	// Owner 가 주문 수락 or 주문 취소 -> 취소 사유 전달
	@PatchMapping("/{orderId}/status")
	@Operation(summary = "주문 상태 변경", description = "Owner가 주문을 수락하거나 취소합니다. Customer는 주문 생성 5분 이내 && Owner가 주문 상태를 변경하기 전에만 취소할 수 있습니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "주문 상태 변경 성공"),
		@ApiResponse(responseCode = "400", description = "주문이 존재하지 않음 또는 잘못된 상태 변경 요청"),
		@ApiResponse(responseCode = "403", description = "Owner 또는 고객 권한 없음")
	})
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
	@Operation(summary = "주문 내역 삭제", description = "Customer가 자신의 주문 내역을 삭제합니다. 진행 중인 주문은 삭제할 수 없습니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "주문 삭제 성공"),
		@ApiResponse(responseCode = "400", description = "주문이 존재하지 않거나 진행 중인 주문"),
		@ApiResponse(responseCode = "403", description = "본인 주문이 아님")
	})
	public ResponseEntity<Void> deleteOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID orderId
	) {
		orderService.deleteOrder(userDetails.getUser(), orderId);
		return ResponseEntity.noContent().build();
	}

	/* 주문 수정은 불가: 취소하고 재주문해야함 */
}
