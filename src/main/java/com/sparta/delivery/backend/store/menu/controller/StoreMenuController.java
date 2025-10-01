package com.sparta.delivery.backend.store.menu.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
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

import com.sparta.delivery.backend.store.menu.dto.ReqSortOrderOwnerDto;
import com.sparta.delivery.backend.store.menu.dto.ReqStoreMenuOwnerDto;
import com.sparta.delivery.backend.store.menu.dto.ReqVisibilityOwnerDto;
import com.sparta.delivery.backend.store.menu.dto.ResSortOrderOwnerDto;
import com.sparta.delivery.backend.store.menu.dto.ResStoreMenuOwnerDto;
import com.sparta.delivery.backend.store.menu.dto.ResVisibilityOwnerDto;
import com.sparta.delivery.backend.store.menu.service.StoreMenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/menus")
@RequiredArgsConstructor
public class StoreMenuController {

	private final StoreMenuService storeMenuService;

	@PostMapping
	public ResStoreMenuOwnerDto createStoreMenu(
		@PathVariable UUID storeId,
		@RequestBody ReqStoreMenuOwnerDto reqStoreMenuOwnerDto
	) {
		return storeMenuService.createStoreMenu(storeId, reqStoreMenuOwnerDto);
	}

	@GetMapping
	public Page<ResStoreMenuOwnerDto> getAllStoreMenusByStoreId(
		@PathVariable UUID storeId,
		@RequestParam("page") int page,
		@RequestParam("size") int size
	) {
		return storeMenuService.getStoreMenusByStoreId(storeId,page-1, size);
	}

	@GetMapping("/{menuId}")
	public ResStoreMenuOwnerDto getStoreMenuByStoreMenuId(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId
	) {
		return storeMenuService.getStoreMenuByStoreMenuId(storeId, menuId);
	}

	// 메뉴 정보 수정
	@PutMapping("/{menuId}")
	public ResStoreMenuOwnerDto updateStoreMenu(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody ReqStoreMenuOwnerDto reqStoreMenuOwnerDto
	) {
		return storeMenuService.updateStoreMenu(storeId, menuId, reqStoreMenuOwnerDto);
	}

	// 메뉴 순서 변경
	@PatchMapping("/{menuId}/sort")
	public ResSortOrderOwnerDto updateSortOrder(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody ReqSortOrderOwnerDto reqSortOrderOwnerDto
	) {
		return storeMenuService.updateSortOrder(storeId, menuId, reqSortOrderOwnerDto);
	}

	// 숨기기
	@PatchMapping("/{menuId}/visibility")
	public ResVisibilityOwnerDto updateVisibility(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody ReqVisibilityOwnerDto reqVisibilityOwnerDto
	) {
		return storeMenuService.updateVisibility(storeId, menuId, reqVisibilityOwnerDto);
	}

	@DeleteMapping("/{menuId}")
	public void deleteStoreMenu(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId
	) {
		storeMenuService.deleteStoreMenu(storeId, menuId);
	}
}
