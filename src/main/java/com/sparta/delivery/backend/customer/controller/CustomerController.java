package com.sparta.delivery.backend.customer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.customer.dto.ReqCreateCustomerDto;
import com.sparta.delivery.backend.customer.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
	private final CustomerService customerService;

	@PostMapping
	public ResponseEntity<Void> createCustomer(@Valid @RequestBody ReqCreateCustomerDto requestDto) {
		customerService.createCustomer(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
