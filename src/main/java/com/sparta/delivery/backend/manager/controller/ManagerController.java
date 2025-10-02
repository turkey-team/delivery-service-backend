package com.sparta.delivery.backend.manager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.manager.dto.ReqCreateManagerDto;
import com.sparta.delivery.backend.manager.service.ManagerService;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/managers")
@RequiredArgsConstructor
public class ManagerController {
	private final ManagerService managerService;

	@PostMapping
	//TODO: 추후 개발이 거의 완료 된다면 해당 API @Secured 필요
	//@Secured(UserRoleEnum.Authority.MANAGER)
	public ResponseEntity<Void> createManager(@Valid @RequestBody ReqCreateManagerDto requestDto) {
		managerService.createManager(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
