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

import com.sparta.delivery.backend.store.menu.dto.ReqSortOrderDto;
import com.sparta.delivery.backend.store.menu.dto.ReqStoreMenuCreateDto;
import com.sparta.delivery.backend.store.menu.dto.ReqStoreMenuUpdateDto;
import com.sparta.delivery.backend.store.menu.dto.ReqVisibilityDto;
import com.sparta.delivery.backend.store.menu.dto.ResSortOrderDto;
import com.sparta.delivery.backend.store.menu.dto.ResStoreMenuCreateDto;
import com.sparta.delivery.backend.store.menu.dto.ResStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ResStoreMenuUpdateDto;
import com.sparta.delivery.backend.store.menu.dto.ResVisibilityDto;
import com.sparta.delivery.backend.store.menu.service.StoreMenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/menus")
@RequiredArgsConstructor
public class StoreMenuController {

	private final StoreMenuService storeMenuService;

	@PostMapping
	public ResStoreMenuCreateDto createStoreMenu(
		@PathVariable UUID storeId,
		@RequestBody ReqStoreMenuCreateDto reqStoreMenuCreateDto
	) {
		return storeMenuService.createStoreMenu(storeId, reqStoreMenuCreateDto);
	}

	@GetMapping
	public Page<ResStoreMenuDto> getAllStoreMenusByStoreId(
		@PathVariable UUID storeId,
		@RequestParam("page") int page,
		@RequestParam("size") int size
	) {
		return storeMenuService.getStoreMenusByStoreId(storeId,page-1, size);
	}

	@GetMapping("/{menuId}")
	public ResStoreMenuDto getStoreMenuByStoreMenuId(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId
	) {
		return storeMenuService.getStoreMenuByStoreMenuId(storeId, menuId);
	}

	// 메뉴 정보 수정
	@PutMapping("/{menuId}")
	public ResStoreMenuUpdateDto updateStoreMenu(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody ReqStoreMenuUpdateDto reqStoreMenuUpdateDto
	) {
		return storeMenuService.updateStoreMenu(storeId, menuId, reqStoreMenuUpdateDto);
	}

	// 메뉴 순서 변경
	@PatchMapping("/{menuId}/sort")
	public ResSortOrderDto updateSortOrder(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody ReqSortOrderDto reqSortOrderDto
	) {
		return storeMenuService.updateSortOrder(storeId, menuId, reqSortOrderDto);
	}

	// 숨기기
	@PatchMapping("/{menuId}/visibility")
	public ResVisibilityDto updateVisibility(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody ReqVisibilityDto reqVisibilityDto
	) {
		return storeMenuService.updateVisibility(storeId, menuId, reqVisibilityDto);
	}

	@DeleteMapping("/{menuId}")
	public void deleteStoreMenu(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId
	) {
		storeMenuService.deleteStoreMenu(storeId, menuId);
	}
}
