package com.sparta.delivery.backend.store.menu.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.menu.dto.StoreMenuRequestDto;
import com.sparta.delivery.backend.store.menu.dto.StoreMenuResponseDto;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.repository.StoreMenuRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreMenuService {

	private final StoreMenuRepository storeMenuRepository;
	private final StoreRepository storeRepository;
	private final ImageRepository imageRepository;

	@Transactional
	public StoreMenuResponseDto createStoreMenu(UUID storeId, StoreMenuRequestDto storeMenuRequestDto) {

		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Store not found"));

		Image image = Image.builder()
			.imageUrl(storeMenuRequestDto.getImageUrl())
			.build();
		imageRepository.save(image);

		StoreMenu storeMenu = StoreMenu.builder()
			.storeMenuRequestDto(storeMenuRequestDto)
			.store(store)
			.image(image)
			.build();

		storeMenuRepository.save(storeMenu);
		return new StoreMenuResponseDto(storeMenu);
	}

	@Transactional(readOnly = true)
	public StoreMenuResponseDto getStoreMenuByStoreMenuId(UUID storeId, UUID menuId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Store not found"));

		StoreMenu storeMenu = storeMenuRepository.findById(menuId)
			.orElseThrow(() -> new IllegalArgumentException("StoreMenu not found"));

		return new StoreMenuResponseDto(storeMenu);
	}

	@Transactional(readOnly = true)
	public Page<StoreMenuResponseDto> getStoreMenusByStoreId(UUID storeId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());

		Page<StoreMenu> storeMenuList =
			storeMenuRepository.findAllByStoreId(storeId, pageable);

		return storeMenuList.map(StoreMenuResponseDto::new);
	}

	@Transactional
	public StoreMenuResponseDto updateStoreMenu(UUID storeId, StoreMenuRequestDto dto, UUID menuId) {

		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Store not found"));

		StoreMenu storeMenu = storeMenuRepository.findById(menuId)
			.orElseThrow(() -> new IllegalArgumentException("StoreMenu not found"));

		if (dto.getImageUrl() != null) {
			Image image = storeMenu.getImage();
			if (image == null) {
				image = Image.builder().imageUrl(dto.getImageUrl()).build();
				imageRepository.save(image);
				storeMenu.setImage(image);
			} else if (!dto.getImageUrl().equals(image.getImageUrl())) {
				image.setImageUrl(dto.getImageUrl());
			}
		}

		storeMenu.setName(dto.getName() != null ? dto.getName() : storeMenu.getName());
		storeMenu.setPrice(dto.getPrice() != storeMenu.getPrice() ? dto.getPrice() : storeMenu.getPrice());
		storeMenu.setDescription(dto.getDescription() != null ? dto.getDescription() : storeMenu.getDescription());
		storeMenu.setPrepTime(dto.getPrepTime() != null ? dto.getPrepTime() : storeMenu.getPrepTime());
		storeMenu.setSortOrder(dto.getSortOrder() != storeMenu.getSortOrder() ? dto.getSortOrder() : storeMenu.getSortOrder());
		storeMenu.setStockStatus(dto.getStockStatus() != null ? dto.getStockStatus() : storeMenu.getStockStatus());
		storeMenu.setHiddenAt(dto.getHiddenAt() != null ? dto.getHiddenAt() : storeMenu.getHiddenAt());

		return new StoreMenuResponseDto(storeMenu);
	}

	@Transactional
	public void deleteStoreMenu(UUID storeId, UUID menuId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Store not found"));

		StoreMenu storeMenu = storeMenuRepository.findById(menuId)
			.orElseThrow(() -> new IllegalArgumentException("StoreMenu not found"));

		storeMenuRepository.delete(storeMenu);
	}
}
