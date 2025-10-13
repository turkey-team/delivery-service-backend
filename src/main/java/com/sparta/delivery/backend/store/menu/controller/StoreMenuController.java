package com.sparta.delivery.backend.store.menu.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ReqUpdateSortOrderDto;
import com.sparta.delivery.backend.store.menu.dto.ReqUpdateStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ReqUpdateVisibilityDto;
import com.sparta.delivery.backend.store.menu.dto.ResGetListStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ResGetStoreMenuDto;
import com.sparta.delivery.backend.store.menu.service.StoreMenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/stores/{storeId}/menus")
@RequiredArgsConstructor
public class StoreMenuController {

	private final StoreMenuService storeMenuService;

	@PostMapping
	public ResponseEntity<Void> createStoreMenu(
		@PathVariable UUID storeId,
		@RequestBody ReqCreateStoreMenuDto reqCreateStoreMenuDto
	) {
		storeMenuService.createStoreMenu(storeId, reqCreateStoreMenuDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<Page<ResGetListStoreMenuDto>> getAllStoreMenusByStoreId(
		@PathVariable UUID storeId,
		@RequestParam("page") int page,
		@RequestParam("size") int size
	) {
		Page<ResGetListStoreMenuDto> menus = storeMenuService.getStoreMenusByStoreId(storeId, page - 1, size);
		return ResponseEntity.ok(menus);
	}

	@GetMapping("/{menuId}")
	public ResponseEntity<ResGetStoreMenuDto> getStoreMenuByStoreMenuId(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId
	) {
		ResGetStoreMenuDto menu = storeMenuService.getStoreMenuByStoreMenuId(storeId, menuId);
		return ResponseEntity.ok(menu);
	}

	// 메뉴 정보 수정
	@PutMapping("/{menuId}")
	public ResponseEntity<Void> updateStoreMenu(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody ReqUpdateStoreMenuDto reqUpdateStoreMenuDto
	) {
		storeMenuService.updateStoreMenu(storeId, menuId, reqUpdateStoreMenuDto);
		return ResponseEntity.ok().build();
	}

	// 메뉴 순서 변경
	@PatchMapping("/{menuId}/sort")
	public ResponseEntity<Void> updateSortOrder(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody ReqUpdateSortOrderDto reqUpdateSortOrderDto
	) {
		storeMenuService.updateSortOrder(storeId, menuId, reqUpdateSortOrderDto);
		return ResponseEntity.ok().build();
	}

	// 숨기기
	@PatchMapping("/{menuId}/visibility")
	public ResponseEntity<Void> updateVisibility(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody ReqUpdateVisibilityDto reqUpdateVisibilityDto
	) {
		storeMenuService.updateVisibility(storeId, menuId, reqUpdateVisibilityDto);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{menuId}")
	public ResponseEntity<Void> deleteStoreMenu(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId
	) {
		storeMenuService.deleteStoreMenu(storeId, menuId);
		return ResponseEntity.noContent().build();
	}
}
