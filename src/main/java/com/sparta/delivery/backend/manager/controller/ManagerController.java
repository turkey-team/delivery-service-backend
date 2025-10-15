package com.sparta.delivery.backend.manager.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.manager.dto.ReqCreateManagerDto;
import com.sparta.delivery.backend.manager.dto.ReqUpdateRoleDto;
import com.sparta.delivery.backend.manager.dto.ResGetManagerDetailDto;
import com.sparta.delivery.backend.manager.dto.ResGetManagerSummaryDto;
import com.sparta.delivery.backend.manager.service.ManagerService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/managers")
@RequiredArgsConstructor
public class ManagerController {
	private final ManagerService managerService;

	@PostMapping
	//TODO: 추후 개발이 거의 완료 된다면 해당 API @Secured 필요
	// @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<Void> createManager(@Valid @RequestBody ReqCreateManagerDto requestDto) {
		managerService.createManager(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// MASTER, MANAGER 함께 조회
	@GetMapping
	public ResponseEntity<List<ResGetManagerSummaryDto>> getAllManagers() {
		List<ResGetManagerSummaryDto> responseDtoList = managerService.getAllManagers();
		return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
	}

	@GetMapping("/{managerUserPublicId}")
	public ResponseEntity<ResGetManagerDetailDto> getManager(@PathVariable UUID managerUserPublicId) {
		ResGetManagerDetailDto responseDto = managerService.getManager(managerUserPublicId);
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

	@PatchMapping("/{managerUserPublicId}")
	public ResponseEntity<Void> updateRole(
		@PathVariable UUID managerUserPublicId, @RequestBody ReqUpdateRoleDto requestDto
	) {
		managerService.updateRole(managerUserPublicId, requestDto);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{managerUserPublicId}")
	public ResponseEntity<Void> deleteManager(
		@PathVariable UUID managerUserPublicId, @AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		managerService.deleteManager(managerUserPublicId, userDetails);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
