package com.sparta.delivery.backend.address.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.address.dto.ReqRegisterAddressDto;
import com.sparta.delivery.backend.address.dto.ReqUpdateAddressDto;
import com.sparta.delivery.backend.address.dto.ResAddressDto;
import com.sparta.delivery.backend.address.service.AddressService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "주소(배송지) API V1", description = "주소(배송지) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/addresses")
public class AddressController {

	private final AddressService addressService;

	/**
	 * 주소 등록 API
	 * POST /v1/addresses
	 */
	@Operation(summary = "주소 등록",
		       description = "새로운 주소를 등록합니다. 등록 시 자동으로 기본 주소로 설정됩니다. CUSTOMER 권한 필요")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "주소 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "403", description = "Authorization 헤더가 없거나 권한 없음"),
		@ApiResponse(responseCode = "404", description = "법정동 코드를 찾을 수 없음")
	})
	@PreAuthorize("hasRole('CUSTOMER')")
	@PostMapping
	public ResponseEntity<Void> registerAddress(
		@Valid @RequestBody ReqRegisterAddressDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl user
	) {
		addressService.registerAddress(requestDto, user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 주소 조회(목록) API
	 * GET /v1/addresses
	 */
	@Operation(summary = "주소 목록 조회", description = "내 주소 목록을 조회합니다. CUSTOMER 권한 필요")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(
			array = @ArraySchema(schema = @Schema(implementation = ResAddressDto.class)))),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
		@ApiResponse(responseCode = "403", description = "Authorization 헤더가 없거나 권한 없음", content = @Content)
	})
	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping
	public ResponseEntity<List<ResAddressDto>> getMyAddresses(@AuthenticationPrincipal UserDetailsImpl user) {
		return ResponseEntity.ok(addressService.getMyAddresses(user));
	}

	/**
	 * 기본 주소 조회 API
	 * GET /v1/addresses/default
	 */
	@Operation(summary = "기본 주소 조회", description = "내 기본 주소를 조회합니다. CUSTOMER 권한 필요")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(
			schema = @Schema(implementation = ResAddressDto.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
		@ApiResponse(responseCode = "403", description = "Authorization 헤더가 없거나 권한 없음", content = @Content),
		@ApiResponse(responseCode = "404", description = "기본 배송지를 찾을 수 없음", content = @Content)
	})
	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/default")
	public ResponseEntity<ResAddressDto> getDefaultAddress(@AuthenticationPrincipal UserDetailsImpl user) {
		return ResponseEntity.ok(addressService.getDefaultAddress(user));
	}

	/**
	 * 주소 수정 API
	 * PUT /v1/addresses/{id}
	 */
	@Operation(summary = "주소 수정", description = "내 주소를 수정합니다. CUSTOMER 권한 필요")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(
			schema = @Schema(implementation = ResAddressDto.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
		@ApiResponse(responseCode = "403", description = "Authorization 헤더가 없거나 권한 없음", content = @Content),
		@ApiResponse(responseCode = "404", description = "요청한 리소스를 찾을 수 없음", content = @Content)
	})
	@PreAuthorize("hasRole('CUSTOMER') and @addressPermissionEvaluator.isOwner(#id, authentication.principal)")
	@PutMapping("/{id}")
	public ResponseEntity<ResAddressDto> updateAddress(
		@PathVariable UUID id,
		@Valid @RequestBody ReqUpdateAddressDto requestDto
	) {
		return ResponseEntity.ok(addressService.updateAddress(id, requestDto));
	}

	/**
	 * 기본 주소 설정 API
	 * PUT /v1/addresses/{id}/default
	 */
	@Operation(summary = "기본 주소 설정", description = "기본 주소를 설정합니다. CUSTOMER 권한 필요")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "설정 성공", content = @Content(
			schema = @Schema(implementation = ResAddressDto.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
		@ApiResponse(responseCode = "403", description = "Authorization 헤더가 없거나 권한 없음", content = @Content),
		@ApiResponse(responseCode = "404", description = "요청한 리소스를 찾을 수 없음", content = @Content)
	})
	@PreAuthorize("hasRole('CUSTOMER') and @addressPermissionEvaluator.isOwner(#id, authentication.principal)")
	@PutMapping("/{id}/default")
	public ResponseEntity<ResAddressDto> setDefaultAddress(
		@PathVariable UUID id,
		@AuthenticationPrincipal UserDetailsImpl user
	) {
		return ResponseEntity.ok(addressService.setDefaultAddress(id, user));
	}

	/**
	 * 주소 삭제 API
	 * DELETE /v1/addresses/{id}
	 */
	@Operation(summary = "주소 삭제", description = "주소를 삭제합니다. CUSTOMER 권한 필요")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "삭제 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "403", description = "Authorization 헤더가 없거나 권한 없음"),
		@ApiResponse(responseCode = "404", description = "요청한 리소스를 찾을 수 없음")
	})
	@PreAuthorize("hasRole('CUSTOMER') and @addressPermissionEvaluator.isOwner(#id, authentication.principal)")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAddress(@PathVariable UUID id, @AuthenticationPrincipal UserDetailsImpl user) {
		addressService.deleteAddress(id, user);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
