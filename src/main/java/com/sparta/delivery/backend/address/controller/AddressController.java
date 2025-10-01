package com.sparta.delivery.backend.address.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.address.dto.ReqRegisterAddressDto;
import com.sparta.delivery.backend.address.service.AddressService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/addresses")
public class AddressController {

	private final AddressService addressService;

	@PostMapping
	public ResponseEntity<Void> registerAddress(
		@RequestBody ReqRegisterAddressDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl user
	) {
		addressService.registerAddress(requestDto, user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
