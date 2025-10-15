package com.sparta.delivery.backend.region.controller;

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

import com.sparta.delivery.backend.region.dto.ReqCreateSigunguDto;
import com.sparta.delivery.backend.region.dto.ReqUpdateSigunguDto;
import com.sparta.delivery.backend.region.dto.ResCreateSigunguDto;
import com.sparta.delivery.backend.region.dto.ResReadSigunguDto;
import com.sparta.delivery.backend.region.dto.ResUpdateSigunguDto;
import com.sparta.delivery.backend.region.service.SigunguService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@Tag(name = "Region-Sigungu-Controller", description = "시·군·구 관련 API")
public class SigunguController {

	private final SigunguService sigunguService;

	@Operation(summary = "시·군·구 생성", description = "새로운 시·군·구를 등록합니다. MANAGER만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "시·군·구가 생성되었습니다.",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResCreateSigunguDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "잘못된 요청 형식", value = SIGUNGU_INVALID_JSON),
				@ExampleObject(name = "시·군·구 이름 미입력", value = SIGUNGU_REQUEST_NAME_EMPTY),
				@ExampleObject(name = "시·군·구 이름 유효성 검사 실패", value = SIGUNGU_REQUEST_NAME_INVALID),
				@ExampleObject(name = "중복된 시·군·구 이름 포함", value = SIGUNGU_REQUEST_NAME_DUPLICATE),
				@ExampleObject(name = "시·군·구 코드 미입력", value = SIGUNGU_REQUEST_CODE_EMPTY),
				@ExampleObject(name = "시·군·구 유효성 검사 실패", value = SIGUNGU_REQUEST_CODE_INVALID),
				@ExampleObject(name = "중복된 시·군·구 코드 포함", value = SIGUNGU_REQUEST_CODE_DUPLICATE)
			})),
		@ApiResponse(responseCode = "403", description = "권한이 없습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "권한 부족", value = SIGUNGU_FORBIDDEN)
			})),
		@ApiResponse(responseCode = "404", description = "지역이 존재하지 않습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "시·도 미존재", value = SIDO_NOT_FOUND)
			})),
		@ApiResponse(responseCode = "409", description = "등록된 시·군·구입니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "동일한 시·군·구 이름 존재", value = SIGUNGU_REQUEST_NAME_EXISTS),
				@ExampleObject(name = "동일한 시·군·구 코드 존재", value = SIGUNGU_REQUEST_CODE_EXISTS)
			}))
	})
	@PostMapping("/sidos/{sidoId}/sigungus")
	@PreAuthorize("isAuthenticated() && hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<List<ResCreateSigunguDto>> createSigungus(
		@Parameter(description = "시·도 ID", example = "fba4b623-1f39-425c-98ff-fe739bfbd010") @PathVariable UUID sidoId,
		@RequestBody List<@Valid ReqCreateSigunguDto> requestDtoList
	) {
		List<ResCreateSigunguDto> responseDtoList = sigunguService.createSigungus(sidoId, requestDtoList);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDtoList);
	}

	@Operation(summary = "시·군·구 목록 조회", description = "등록된 시·군·구 목록을 조회합니다. MANAGER, OWNER, CUSTOMER가 사용가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "시·군·구 목록을 조회했습니다.",
			content = @Content(mediaType = "application/json",
				array = @ArraySchema(schema = @Schema(implementation = ResReadSigunguDto.class))
			)),
		@ApiResponse(responseCode = "403", description = "권한이 없습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "권한 부족", value = SIGUNGU_FORBIDDEN)
			})),
		@ApiResponse(responseCode = "404", description = "지역이 존재하지 않습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "시·도 미존재", value = SIDO_NOT_FOUND)
			}))
	})
	@GetMapping("/sidos/{sidoId}/sigungus")
	@PreAuthorize("isAuthenticated() && hasAnyRole('MASTER', 'MANAGER', 'OWNER', 'CUSTOMER')")
	public ResponseEntity<List<ResReadSigunguDto>> getAllSigungu(
		@Parameter(description = "시·도 ID", example = "fba4b623-1f39-425c-98ff-fe739bfbd010") @PathVariable UUID sidoId
	) {
		List<ResReadSigunguDto> responseDtoList = sigunguService.getAllSigungu(sidoId);

		return ResponseEntity.ok(responseDtoList);
	}

	@Operation(summary = "시·군·구 수정", description = "기존의 시·군·구를 수정합니다. MANAGER만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "시·군·구가 수정되었습니다.",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResUpdateSigunguDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "잘못된 요청 형식", value = SIGUNGU_INVALID_JSON),
				@ExampleObject(name = "시·군·구 이름 미입력", value = SIGUNGU_REQUEST_NAME_EMPTY),
				@ExampleObject(name = "시·군·구 이름 유효성 검사 실패", value = SIGUNGU_REQUEST_NAME_INVALID),
				@ExampleObject(name = "시·군·구 코드 미입력", value = SIGUNGU_REQUEST_CODE_EMPTY),
				@ExampleObject(name = "시·군·구 유효성 검사 실패", value = SIGUNGU_REQUEST_CODE_INVALID),
			})),
		@ApiResponse(responseCode = "403", description = "권한이 없습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "권한 부족", value = SIGUNGU_FORBIDDEN)
			})),
		@ApiResponse(responseCode = "404", description = "지역이 존재하지 않습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "시·도 미존재", value = SIDO_NOT_FOUND),
				@ExampleObject(name = "시·군·구 미존재", value = SIGUNGU_NOT_FOUND)
			})),
		@ApiResponse(responseCode = "409", description = "등록된 시·군·구입니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "동일한 시·군·구 이름 존재", value = SIGUNGU_REQUEST_NAME_EXISTS),
				@ExampleObject(name = "동일한 시·군·구 코드 존재", value = SIGUNGU_REQUEST_CODE_EXISTS)
			}))
	})
	@PutMapping("/sidos/{sidoId}/sigungus/{sigunguId}")
	@PreAuthorize("isAuthenticated() && hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<ResUpdateSigunguDto> updateSigungu(
		@Parameter(description = "시·도 ID", example = "fba4b623-1f39-425c-98ff-fe739bfbd010") @PathVariable UUID sidoId,
		@Parameter(description = "시·군·구 ID", example = "cc1093ef-66c9-419d-a013-9718ae639a6b") @PathVariable UUID sigunguId,
		@Valid @RequestBody ReqUpdateSigunguDto requestDto
	) {
		ResUpdateSigunguDto responseDto = sigunguService.updateSigungu(sidoId, sigunguId, requestDto);

		return ResponseEntity.ok(responseDto);
	}

	@Operation(summary = "시·군·구 삭제", description = "기존의 시·군·구를 삭제합니다. MANAGER만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "시·군·구가 삭제되었습니다.",
			content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한이 없습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "권한 부족", value = SIGUNGU_FORBIDDEN)
			})),
		@ApiResponse(responseCode = "404", description = "지역이 존재하지 않습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "시·도 미존재", value = SIDO_NOT_FOUND),
				@ExampleObject(name = "시·군·구 미존재", value = SIGUNGU_NOT_FOUND)
			}))
	})
	@DeleteMapping("/sidos/{sidoId}/sigungus/{sigunguId}")
	@PreAuthorize("isAuthenticated() && hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<Void> deleteSigungu(
		@Parameter(description = "시·도 ID", example = "fba4b623-1f39-425c-98ff-fe739bfbd010") @PathVariable UUID sidoId,
		@Parameter(description = "시·군·구 ID", example = "cc1093ef-66c9-419d-a013-9718ae639a6b") @PathVariable UUID sigunguId,
		@AuthenticationPrincipal UserDetailsImpl loginUser
	) {
		sigunguService.deleteSigungu(sidoId, sigunguId, loginUser.getId());

		return ResponseEntity.noContent().build();
	}

}
