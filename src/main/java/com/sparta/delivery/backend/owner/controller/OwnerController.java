package com.sparta.delivery.backend.owner.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.owner.dto.ReqCreateOwnerDto;
import com.sparta.delivery.backend.owner.service.OwnerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/owners")
@RequiredArgsConstructor
public class OwnerController {

	private final OwnerService ownerService;

	@PostMapping
	public ResponseEntity<Void> createOwner(@Valid @RequestBody ReqCreateOwnerDto requestDto) {
		ownerService.createOwner(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
