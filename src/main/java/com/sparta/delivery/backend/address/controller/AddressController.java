package com.sparta.delivery.backend.address.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
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

	@PostMapping
	public ResponseEntity<Void> registerAddress(
		@Valid @RequestBody ReqRegisterAddressDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl user
	) {
		addressService.registerAddress(requestDto, user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public List<ResAddressDto> getMyAddresses(@AuthenticationPrincipal UserDetailsImpl user) {
		return addressService.getMyAddresses(user);
	}

	@PreAuthorize("@addressPermissionEvaluator.isOwner(#id, authentication.principal)")
	@PutMapping("/{id}")
	public ResAddressDto updateAddress(
		@PathVariable UUID id,
		@Valid @RequestBody ReqUpdateAddressDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl user
	) {
		return addressService.updateAddress(id, requestDto, user);
	}

	@PreAuthorize("@addressPermissionEvaluator.isOwner(#id, authentication.principal)")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAddress(@PathVariable UUID id, @AuthenticationPrincipal UserDetailsImpl user) {
		addressService.deleteAddress(id, user);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
