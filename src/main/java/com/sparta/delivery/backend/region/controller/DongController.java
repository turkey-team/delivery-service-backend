package com.sparta.delivery.backend.region.controller;

import static com.sparta.delivery.backend.region.internal.DongSwaggerMessage.*;
import static com.sparta.delivery.backend.region.internal.SidoSwaggerMessage.*;
import static com.sparta.delivery.backend.region.internal.SigunguSwaggerMessage.*;

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

import com.sparta.delivery.backend.region.dto.ReqCreateDongDto;
import com.sparta.delivery.backend.region.dto.ReqUpdateDongDto;
import com.sparta.delivery.backend.region.dto.ResCreateDongDto;
import com.sparta.delivery.backend.region.dto.ResReadDongDto;
import com.sparta.delivery.backend.region.dto.ResUpdateDongDto;
import com.sparta.delivery.backend.region.service.DongService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/regions")
@RequiredArgsConstructor
@Tag(name = "Region-Dong-Controller", description = "동 관련 API")
public class DongController {

	private final DongService dongService;

	@Operation(summary = "동 생성", description = "새로운 동을 등록합니다. MANAGER만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "동이 생성되었습니다.",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResCreateDongDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "잘못된 요청 형식", value = DONG_INVALID_JSON),
				@ExampleObject(name = "동 이름 미입력", value = DONG_REQUEST_NAME_EMPTY),
				@ExampleObject(name = "동 이름 유효성 검사 실패", value = DONG_REQUEST_NAME_INVALID),
				@ExampleObject(name = "중복된 동 이름 포함", value = DONG_REQUEST_NAME_DUPLICATE),
				@ExampleObject(name = "동 코드 미입력", value = DONG_REQUEST_CODE_EMPTY),
				@ExampleObject(name = "동 유효성 검사 실패", value = DONG_REQUEST_CODE_INVALID),
				@ExampleObject(name = "중복된 동 코드 포함", value = DONG_REQUEST_CODE_DUPLICATE)
			})),
		@ApiResponse(responseCode = "403", description = "권한이 없습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "권한 부족", value = DONG_FORBIDDEN)
			})),
		@ApiResponse(responseCode = "404", description = "지역이 존재하지 않습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "시·군·구 미존재", value = SIGUNGU_NOT_FOUND)
			})),
		@ApiResponse(responseCode = "409", description = "등록된 동입니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "동일한 동 이름 존재", value = DONG_REQUEST_NAME_EXISTS),
				@ExampleObject(name = "동일한 동 코드 존재", value = DONG_REQUEST_CODE_EXISTS)
			}))
	})
	@PostMapping("/sigungus/{sigunguId}/dongs")
	@PreAuthorize("isAuthenticated() && hasRole('MANAGER')")
	public ResponseEntity<List<ResCreateDongDto>> createDongs(
		@Parameter(description = "시·군·구 ID", example = "cc1093ef-66c9-419d-a013-9718ae639a6b") @PathVariable UUID sigunguId,
		@RequestBody List<@Valid ReqCreateDongDto> requestDtoList
	) {
		List<ResCreateDongDto> responseDtoList = dongService.createDongs(sigunguId, requestDtoList);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDtoList);
	}

	@Operation(summary = "동 목록 조회", description = "등록된 동 목록을 조회합니다. MANAGER, OWNER, CUSTOMER가 사용가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "동 목록을 조회했습니다.",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResReadDongDto.class))),
		@ApiResponse(responseCode = "403", description = "권한이 없습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "권한 부족", value = DONG_FORBIDDEN)
			})),
		@ApiResponse(responseCode = "404", description = "지역이 존재하지 않습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "시·군·구 미존재", value = SIGUNGU_NOT_FOUND)
			})),
	})
	@GetMapping("/sigungus/{sigunguId}/dongs")
	@PreAuthorize("isAuthenticated() && hasAnyRole('MANAGER', 'OWNER', 'CUSTOMER', 'MASTER')")
	public ResponseEntity<List<ResReadDongDto>> getAllDong(
		@Parameter(description = "시·군·구 ID", example = "cc1093ef-66c9-419d-a013-9718ae639a6b") @PathVariable UUID sigunguId
	) {
		List<ResReadDongDto> responseDtoList = dongService.getAllDong(sigunguId);

		return ResponseEntity.ok(responseDtoList);
	}

	@Operation(summary = "동 수정", description = "기존의 동을 수정합니다. MANAGER만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "동이 수정되었습니다.",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResUpdateDongDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "잘못된 요청 형식", value = DONG_INVALID_JSON),
				@ExampleObject(name = "동 이름 미입력", value = DONG_REQUEST_NAME_EMPTY),
				@ExampleObject(name = "동 이름 유효성 검사 실패", value = DONG_REQUEST_NAME_INVALID),
				@ExampleObject(name = "동 코드 미입력", value = DONG_REQUEST_CODE_EMPTY),
				@ExampleObject(name = "동 유효성 검사 실패", value = DONG_REQUEST_CODE_INVALID)
			})),
		@ApiResponse(responseCode = "403", description = "권한이 없습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "권한 부족", value = DONG_FORBIDDEN)
			})),
		@ApiResponse(responseCode = "404", description = "지역이 존재하지 않습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "시·군·구 미존재", value = SIGUNGU_NOT_FOUND),
				@ExampleObject(name = "동 미존재", value = DONG_NOT_FOUND)
			})),
		@ApiResponse(responseCode = "409", description = "등록된 동입니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "동일한 동 이름 존재", value = DONG_REQUEST_NAME_EXISTS),
				@ExampleObject(name = "동일한 동 코드 존재", value = DONG_REQUEST_CODE_EXISTS)
			}))
	})
	@PutMapping("/sigungus/{sigunguId}/dongs/{dongId}")
	@PreAuthorize("isAuthenticated() && hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<ResUpdateDongDto> updateDong(
		@Parameter(description = "시·군·구 ID", example = "cc1093ef-66c9-419d-a013-9718ae639a6b") @PathVariable UUID sigunguId,
		@Parameter(description = "동 ID", example = "c373ba20-9e15-41fd-acdd-b180aba31b12") @PathVariable UUID dongId,
		@Valid @RequestBody ReqUpdateDongDto requestDto
	) {
		ResUpdateDongDto responseDto = dongService.updateDong(sigunguId, dongId, requestDto);

		return ResponseEntity.ok(responseDto);
	}

	@Operation(summary = "동 삭제", description = "기존의 동을 삭제합니다. MANAGER만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "동이 삭제되었습니다.",
			content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한이 없습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "권한 부족", value = SIDO_FORBIDDEN)
			})),
		@ApiResponse(responseCode = "404", description = "지역이 존재하지 않습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "시·군·구 미존재", value = SIGUNGU_NOT_FOUND),
				@ExampleObject(name = "동 미존재", value = DONG_NOT_FOUND)
			}))
	})
	@DeleteMapping("/sigungus/{sigunguId}/dongs/{dongId}")
	@PreAuthorize("isAuthenticated() && hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<Void> deleteDong(
		@Parameter(description = "시·군·구 ID", example = "cc1093ef-66c9-419d-a013-9718ae639a6b") @PathVariable UUID sigunguId,
		@Parameter(description = "동 ID", example = "c373ba20-9e15-41fd-acdd-b180aba31b12") @PathVariable UUID dongId,
		@AuthenticationPrincipal UserDetailsImpl loginUser
	) {
		dongService.deleteDong(sigunguId, dongId, loginUser.getId());

		return ResponseEntity.noContent().build();
	}

}
