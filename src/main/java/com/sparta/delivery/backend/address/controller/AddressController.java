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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/addresses")
public class AddressController {

	private final AddressService addressService;

	/**
	 * 주소 등록 API
	 * POST /v1/addresses
	 */
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
	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping
	public ResponseEntity<List<ResAddressDto>> getMyAddresses(@AuthenticationPrincipal UserDetailsImpl user) {
		return ResponseEntity.ok(addressService.getMyAddresses(user));
	}

	/**
	 * 기본 배송지 조회 API
	 * GET /v1/addresses/default
	 */
	@PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/default")
	public ResponseEntity<ResAddressDto> getDefaultAddress(@AuthenticationPrincipal UserDetailsImpl user) {
		return ResponseEntity.ok(addressService.getDefaultAddress(user));
	}

	/**
	 * 주소 수정 API
	 * PUT /v1/addresses/{id}
	 */
	@PreAuthorize("hasRole('CUSTOMER') and @addressPermissionEvaluator.isOwner(#id, authentication.principal)")
	@PutMapping("/{id}")
	public ResAddressDto updateAddress(
		@PathVariable UUID id,
		@Valid @RequestBody ReqUpdateAddressDto requestDto
	) {
		return addressService.updateAddress(id, requestDto);
	}

	/**
	 * 기본 배송지 설정 API
	 * PUT /v1/addresses/{id}/default
	 */
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
	@PreAuthorize("hasRole('CUSTOMER') and @addressPermissionEvaluator.isOwner(#id, authentication.principal)")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAddress(@PathVariable UUID id, @AuthenticationPrincipal UserDetailsImpl user) {
		addressService.deleteAddress(id, user);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
