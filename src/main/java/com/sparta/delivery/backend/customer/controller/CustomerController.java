package com.sparta.delivery.backend.customer.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.customer.dto.ReqChangePasswordDto;
import com.sparta.delivery.backend.customer.dto.ReqCreateCustomerDto;
import com.sparta.delivery.backend.customer.dto.ReqPasswordResetDto;
import com.sparta.delivery.backend.customer.dto.ReqPasswordResetRequestDto;
import com.sparta.delivery.backend.customer.dto.ReqUpdateCustomerDto;
import com.sparta.delivery.backend.customer.dto.ResGetCustomerDto;
import com.sparta.delivery.backend.customer.dto.ResGetMyCustomerDto;
import com.sparta.delivery.backend.customer.dto.ResPasswordResetRequestDto;
import com.sparta.delivery.backend.customer.service.CustomerService;
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
		@ApiResponse(responseCode = "404", description = "고객 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@Secured(UserRoleEnum.Authority.CUSTOMER)
	@GetMapping("/me")
	public ResponseEntity<ResGetMyCustomerDto> getCurrentCustomer(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		ResGetMyCustomerDto response = customerService.getCurrentCustomer(userDetails);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "고객 정보 조회 (관리자용)", description = "관리자가 특정 고객의 정보를 조회합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "404", description = "고객 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	@GetMapping("/{customerUserPublicId}")
	public ResponseEntity<ResGetCustomerDto> getCustomerByUserPublicId(@PathVariable UUID customerUserPublicId) {
		ResGetCustomerDto response = customerService.getCustomerByUserPublicId(customerUserPublicId);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "내 정보 수정", description = "로그인한 고객의 정보를 수정합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "404", description = "고객 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@Secured(UserRoleEnum.Authority.CUSTOMER)
	@PatchMapping("/me")
	public ResponseEntity<ResGetMyCustomerDto> updateCurrentCustomer(
		@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody ReqUpdateCustomerDto requestDto) {
		customerService.updateCurrentCustomer(userDetails, requestDto);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "비밀번호 변경", description = "로그인한 상태에서 비밀번호를 변경합니다 (기존 비밀번호 필요)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "404", description = "고객 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@Secured(UserRoleEnum.Authority.CUSTOMER)
	@PatchMapping("/me/password")
	public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody ReqChangePasswordDto requestDto
	) {
		customerService.changePassword(userDetails, requestDto);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "비밀번호 재설정 요청", description = "이메일로 비밀번호 재설정 링크를 발송합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "요청이 처리되었습니다"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content())
	})
	@PostMapping("/password-reset/request")
	public ResponseEntity<ResPasswordResetRequestDto> requestPasswordReset(
		@Valid @RequestBody ReqPasswordResetRequestDto requestDto
	) {
		customerService.requestPasswordReset(requestDto);
		return ResponseEntity.ok(new ResPasswordResetRequestDto(
			"해당 이메일로 가입된 계정이 있다면 비밀번호 재설정 링크가 발송됩니다."
		));
	}

	@Operation(summary = "비밀번호 재설정 확인", description = "토큰을 사용하여 새 비밀번호로 재설정합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "재설정 성공"),
		@ApiResponse(responseCode = "400", description = "유효하지 않거나 만료된 토큰", content = @Content()),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content())
	})
	@PostMapping("/password-reset/confirm")
	public ResponseEntity<Void> resetPassword(@Valid @RequestBody ReqPasswordResetDto requestDto) {
		customerService.resetPassword(requestDto);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "내 계정 탈퇴", description = "로그인한 고객이 본인 계정을 탈퇴합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "탈퇴 성공"),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@Secured(UserRoleEnum.Authority.CUSTOMER)
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteCurrentCustomer(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		customerService.deleteCurrentCustomer(userDetails);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "고객 탈퇴 (관리자용)", description = "관리자가 특정 고객을 탈퇴 처리합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "탈퇴 처리 완료"),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "404", description = "고객을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	@DeleteMapping("/{customerUserPublicId}")
	public ResponseEntity<Void> deleteCustomerByManager(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID customerUserPublicId) {
		customerService.deleteCustomerByManager(userDetails, customerUserPublicId);
		return ResponseEntity.ok().build();
	}
}
