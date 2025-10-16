package com.sparta.delivery.backend.customer.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.customer.dto.ReqCreateCustomerAddressDto;
import com.sparta.delivery.backend.customer.dto.ReqUpdateCustomerAddressDto;
import com.sparta.delivery.backend.customer.dto.ResCustomerAddressDto;
import com.sparta.delivery.backend.customer.dto.ResDefaultAddressDto;
import com.sparta.delivery.backend.customer.service.CustomerAddressService;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/customers/addresses")
@RequiredArgsConstructor
@Tag(name = "고객 배송지 API V1", description = "고객 배송지 관리 API")
public class CustomerAddressController {

	private final CustomerAddressService customerAddressService;

	@Operation(summary = "배송지 등록", description = "새로운 배송지를 등록합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@Secured(UserRoleEnum.Authority.CUSTOMER)
	@PostMapping
	public ResponseEntity<Void> registerAddress(
		@Valid @RequestBody ReqCreateCustomerAddressDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		customerAddressService.createCustomerAddress(requestDto, userDetails);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "내 배송지 목록 조회", description = "로그인한 고객의 모든 배송지를 조회합니다 (기본 배송지 우선, 최신순)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@Secured(UserRoleEnum.Authority.CUSTOMER)
	@GetMapping
	public ResponseEntity<List<ResCustomerAddressDto>> getMyAddresses(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		List<ResCustomerAddressDto> response = customerAddressService.getMyCustomerAddresses(userDetails);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "기본 배송지 조회", description = "로그인한 고객의 기본 배송지를 조회합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "404", description = "기본 배송지가 설정되지 않음", content = @Content(schema = @Schema(hidden = true)))
	})
	@Secured(UserRoleEnum.Authority.CUSTOMER)
	@GetMapping("/default")
	public ResponseEntity<ResDefaultAddressDto> getDefaultAddress(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		ResDefaultAddressDto response = customerAddressService.getDefaultCustomerAddress(userDetails);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "기본 배송지 설정", description = "특정 배송지를 기본 배송지로 설정합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "설정 성공"),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@Secured(UserRoleEnum.Authority.CUSTOMER)
	@PatchMapping("/{customerAddressId}/default")
	public ResponseEntity<Void> setDefaultAddress(
		@PathVariable UUID customerAddressId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		customerAddressService.setDefaultAddress(customerAddressId, userDetails);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "배송지 수정", description = "배송지 정보를 수정합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@Secured(UserRoleEnum.Authority.CUSTOMER)
	@PutMapping("/{customerAddressId}")
	public ResponseEntity<Void> updateAddress(
		@PathVariable UUID customerAddressId,
		@Valid @RequestBody ReqUpdateCustomerAddressDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		customerAddressService.updateCustomerAddress(customerAddressId, requestDto,
			userDetails);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "배송지 삭제", description = "배송지를 삭제합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "삭제 성공"),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@Secured(UserRoleEnum.Authority.CUSTOMER)
	@DeleteMapping("/{customerAddressId}")
	public ResponseEntity<Void> deleteAddress(
		@PathVariable UUID customerAddressId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		customerAddressService.deleteCustomerAddress(customerAddressId, userDetails);
		return ResponseEntity.noContent().build();
	}
}
