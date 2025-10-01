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

import com.sparta.delivery.backend.store.menu.dto.StoreMenuRequestDto;
import com.sparta.delivery.backend.store.menu.dto.StoreMenuResponseDto;
import com.sparta.delivery.backend.store.menu.service.StoreMenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/menus")
@RequiredArgsConstructor
public class StoreMenuController {

	private final StoreMenuService storeMenuService;

	@PostMapping
	public StoreMenuResponseDto createStoreMenu(@PathVariable UUID storeId,
		@RequestBody StoreMenuRequestDto storeMenuRequestDto) {
		return storeMenuService.createStoreMenu(storeId, storeMenuRequestDto);
	}

	// 메뉴 특성상 한번 생성하면, 세부 설정 몇개만 건드는 경우(숨기기, 재고 변경 등)가 많기 때문에 부분 수정하는 PATCH 적용
	@PatchMapping("/{menuId}")
	public StoreMenuResponseDto updateStoreMenu(@PathVariable UUID storeId,
		@RequestBody StoreMenuRequestDto storeMenuRequestDto, @PathVariable UUID menuId) {
		return storeMenuService.updateStoreMenu(storeId, storeMenuRequestDto, menuId);
	}

	@GetMapping
	public Page<StoreMenuResponseDto> getAllStoreMenusByStoreId(
		@PathVariable UUID storeId,
		@RequestParam("page") int page,
		@RequestParam("size") int size
	) {
		return storeMenuService.getStoreMenusByStoreId(storeId,page-1, size);
	}

	@GetMapping("/{menuId}")
	public StoreMenuResponseDto getStoreMenuByStoreMenuId(@PathVariable UUID storeId,
		@PathVariable UUID menuId) {
		return storeMenuService.getStoreMenuByStoreMenuId(storeId, menuId);
	}

	@DeleteMapping("/{menuId}")
	public void deleteStoreMenu(@PathVariable UUID storeId,
		@PathVariable UUID menuId) {
		storeMenuService.deleteStoreMenu(storeId, menuId);
	}
}
