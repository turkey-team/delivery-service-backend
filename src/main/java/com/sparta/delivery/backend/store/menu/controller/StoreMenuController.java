package com.sparta.delivery.backend.store.menu.controller;

import com.sparta.delivery.backend.global.common.dto.PageResponse;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.store.menu.dto.*;
import com.sparta.delivery.backend.store.menu.service.StoreMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/stores/{storeId}/menus")
@RequiredArgsConstructor
@Tag(name="Store-Menu-Controller", description = "가게 메뉴 관련 API")
public class StoreMenuController {

	private final StoreMenuService storeMenuService;

	@PostMapping
	@Operation(summary = "가게 메뉴 추가", description = "해당 가게에 새로운 메뉴를 추가합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "메뉴 생성 성공"),
			@ApiResponse(responseCode = "400", description = "가게가 존재하지 않거나 메뉴 이름 중복"),
			@ApiResponse(responseCode = "403", description = "권한 없음")
	})
	@PreAuthorize("hasAnyRole('OWNER', 'MASTER', 'MANAGER')")
	public ResponseEntity<Void> createStoreMenu(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID storeId,
		@RequestBody ReqCreateStoreMenuDto reqCreateStoreMenuDto
	) {
		storeMenuService.createStoreMenu(userDetails.getUser(), storeId, reqCreateStoreMenuDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	@Operation(summary = "가게 메뉴 목록 조회", description = "해당 가게의 모든 메뉴를 조회합니다. 페이징 가능.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "메뉴 목록 조회 성공"),
			@ApiResponse(responseCode = "400", description = "가게 없음")
	})
	@PreAuthorize("hasAnyRole(isAuthenticated())")
	public ResponseEntity<PageResponse<ResGetListStoreMenuDto>> getAllStoreMenusByStoreId(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID storeId,
		@RequestParam("page") int page,
		@RequestParam("size") int size
	) {
		PageResponse<ResGetListStoreMenuDto> menus = storeMenuService.getStoreMenusByStoreId(userDetails.getUser(), storeId, page - 1, size);
		return ResponseEntity.ok(menus);
	}

	@GetMapping("/{menuId}")
	@Operation(summary = "가게 메뉴 상세 조회", description = "해당 가게의 특정 메뉴 정보를 조회합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "메뉴 조회 성공", content = @Content(schema = @Schema(implementation = ResGetStoreMenuDto.class))),
			@ApiResponse(responseCode = "400", description = "가게 또는 메뉴 없음")
	})
	@PreAuthorize("hasAnyRole(isAuthenticated())")
	public ResponseEntity<ResGetStoreMenuDto> getStoreMenuByStoreMenuId(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId
	) {
		ResGetStoreMenuDto menu = storeMenuService.getStoreMenuByStoreMenuId(storeId, menuId);
		return ResponseEntity.ok(menu);
	}

	// 메뉴 정보 수정
	@PutMapping("/{menuId}")
	@Operation(summary = "가게 메뉴 정보 수정", description = "해당 가게의 메뉴 정보를 수정합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "메뉴 수정 성공"),
			@ApiResponse(responseCode = "400", description = "가게 또는 메뉴 없음"),
			@ApiResponse(responseCode = "403", description = "권한 없음")
	})
	@PreAuthorize("hasAnyRole('OWNER', 'MASTER', 'MANAGER')")
	public ResponseEntity<Void> updateStoreMenu(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody ReqUpdateStoreMenuDto reqUpdateStoreMenuDto
	) {
		storeMenuService.updateStoreMenu(userDetails.getUser(), storeId, menuId, reqUpdateStoreMenuDto);
		return ResponseEntity.ok().build();
	}

	// 메뉴 순서 변경
	@PatchMapping("/{menuId}/sort")
	@Operation(summary = "가게 메뉴 순서 변경", description = "메뉴의 순서를 변경합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "순서 변경 성공"),
			@ApiResponse(responseCode = "400", description = "가게 또는 메뉴 없음"),
			@ApiResponse(responseCode = "403", description = "권한 없음")
	})
	@PreAuthorize("hasAnyRole('OWNER', 'MASTER', 'MANAGER')")
	public ResponseEntity<Void> updateSortOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody ReqUpdateSortOrderDto reqUpdateSortOrderDto
	) {
		storeMenuService.updateSortOrder(userDetails.getUser(), storeId, menuId, reqUpdateSortOrderDto);
		return ResponseEntity.ok().build();
	}

	// 숨기기
	@PatchMapping("/{menuId}/visibility")
	@Operation(summary = "가게 메뉴 숨기기/보이기", description = "메뉴의 숨기기/보이기 상태를 변경합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "숨기기/보이기 변경 성공"),
			@ApiResponse(responseCode = "400", description = "가게 또는 메뉴 없음"),
			@ApiResponse(responseCode = "403", description = "권한 없음")
	})
	@PreAuthorize("hasAnyRole('OWNER', 'MASTER', 'MANAGER')")
	public ResponseEntity<Void> updateVisibility(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody ReqUpdateVisibilityDto reqUpdateVisibilityDto
	) {
		storeMenuService.updateVisibility(userDetails.getUser(), storeId, menuId, reqUpdateVisibilityDto);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{menuId}")
	@Operation(summary = "가게 메뉴 삭제", description = "해당 가게의 메뉴를 삭제합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "메뉴 삭제 성공"),
			@ApiResponse(responseCode = "400", description = "가게 또는 메뉴 없음"),
			@ApiResponse(responseCode = "403", description = "권한 없음")
	})
	@PreAuthorize("hasAnyRole('OWNER', 'MASTER', 'MANAGER')")
	public ResponseEntity<Void> deleteStoreMenu(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID storeId,
		@PathVariable UUID menuId
	) {
		storeMenuService.deleteStoreMenu(userDetails.getUser(), storeId, menuId);
		return ResponseEntity.noContent().build();
	}
}
