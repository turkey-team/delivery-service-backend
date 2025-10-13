package com.sparta.delivery.backend.customer.controller;


import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.customer.dto.ReqCreateCustomerDto;
import com.sparta.delivery.backend.customer.dto.ResGetCustomerDto;
import com.sparta.delivery.backend.customer.dto.ResGetMyCustomerDto;
import com.sparta.delivery.backend.customer.service.CustomerService;
import com.sparta.delivery.backend.global.excpetion.ApiException;
import com.sparta.delivery.backend.global.excpetion.DuplicateUsernameException;
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
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
@Tag(name = "고객 API V1", description = "고객 관련 API")
public class CustomerController {
	private final CustomerService customerService;

	@Operation(summary = "고객 회원가입", description = "새로운 고객을 등록합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "회원가입 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "409", description = "이미 존재하는 사용자")
	})
	@PostMapping
	public ResponseEntity<Void> createCustomer(@Valid @RequestBody ReqCreateCustomerDto requestDto) {
		customerService.createCustomer(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "내 정보 조회", description = "로그인한 고객의 마이페이지 정보를 조회합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "404", description = "고객 정보를 찾을 수 없음",content = @Content(schema = @Schema(hidden = true)))
	})
	@Secured(UserRoleEnum.Authority.CUSTOMER)
	@GetMapping("/me")
	public ResponseEntity<ResGetMyCustomerDto> getCurrentCustomer(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		ResGetMyCustomerDto response = customerService.getCurrentCustomer(userDetails);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "고객 정보 조회 (관리자용)", description = "관리자가 특정 고객의 정보를 조회합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "404", description = "고객 정보를 찾을 수 없음",content = @Content(schema = @Schema(hidden = true)))
	})
	@Secured(UserRoleEnum.Authority.MANAGER)
	@GetMapping("/{customerUserPublicId}")
	public ResponseEntity<ResGetCustomerDto> getCustomerByUserPublicId(@PathVariable UUID customerUserPublicId) {
		ResGetCustomerDto response = customerService.getCustomerByUserPublicId(customerUserPublicId);
		return ResponseEntity.ok(response);
	}
}
