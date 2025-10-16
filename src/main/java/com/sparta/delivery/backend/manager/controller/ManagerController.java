package com.sparta.delivery.backend.manager.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.sparta.delivery.backend.manager.dto.ReqCreateManagerDto;
import com.sparta.delivery.backend.manager.dto.ReqUpdateManagerDto;
import com.sparta.delivery.backend.manager.dto.ResGetManagerDetailDto;
import com.sparta.delivery.backend.manager.dto.ResGetManagerSummaryDto;
import com.sparta.delivery.backend.manager.service.ManagerService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/managers")
@RequiredArgsConstructor
public class ManagerController {
	private final ManagerService managerService;

	@Operation(summary = "관리자 생성", description = "관리자를 생성합니다. MASTER만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "409", description = "이미 존재하는 관리자", content = @Content(schema = @Schema(hidden = true))),
	})
	@PostMapping
	@PreAuthorize("isAuthenticated() && hasRole('MASTER')")
	public ResponseEntity<Void> createManager(@Valid @RequestBody ReqCreateManagerDto requestDto) {
		managerService.createManager(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "관리자 목록 조회", description = "관리자 목록을 조회합니다. MASTER와 MANAGER만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json",
			array = @ArraySchema(schema = @Schema(implementation = ResGetManagerSummaryDto.class))
		)),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@GetMapping
	@PreAuthorize("isAuthenticated() && hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<List<ResGetManagerSummaryDto>> getAllManagers() {
		List<ResGetManagerSummaryDto> responseDtoList = managerService.getAllManagers();
		return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
	}

	@Operation(summary = "관리자 상세 조회", description = "관리자의 상세 정보를 조회합니다. MASTER와 MANAGER만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = ResGetManagerDetailDto.class)
		)),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "404", description = "관리자 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@GetMapping("/{managerUserPublicId}")
	@PreAuthorize("isAuthenticated() && hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<ResGetManagerDetailDto> getManager(
		@Parameter(description = "managerPublicId", example = "2863fc2e-a7ed-44db-bc61-2b831995691e")
		@PathVariable UUID managerUserPublicId
	) {
		ResGetManagerDetailDto responseDto = managerService.getManager(managerUserPublicId);
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

	@Operation(summary = "관리자 수정", description = "관리자를 수정합니다. MASTER만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "404", description = "관리자 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "409", description = "중복 발생", content = @Content(schema = @Schema(hidden = true)))
	})
	@PatchMapping("/{managerUserPublicId}")
	@PreAuthorize("isAuthenticated() && hasRole('MASTER')")
	public ResponseEntity<Void> updateManager(
		@Parameter(description = "managerPublicId", example = "2863fc2e-a7ed-44db-bc61-2b831995691e")
		@PathVariable UUID managerUserPublicId, @Valid @RequestBody ReqUpdateManagerDto requestDto
	) {
		managerService.updateManager(managerUserPublicId, requestDto);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Operation(summary = "관리자 삭제", description = "관리자를 삭제합니다. MASTER만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "삭제 성공"),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(hidden = true))),
		@ApiResponse(responseCode = "404", description = "관리자 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
	})
	@DeleteMapping("/{managerUserPublicId}")
	@PreAuthorize("isAuthenticated() && hasRole('MASTER')")
	public ResponseEntity<Void> deleteManager(
		@Parameter(description = "managerPublicId", example = "2863fc2e-a7ed-44db-bc61-2b831995691e")
		@PathVariable UUID managerUserPublicId, @AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		managerService.deleteManager(managerUserPublicId, userDetails);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
