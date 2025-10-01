package com.sparta.delivery.backend.store.menu.service;

import java.time.Instant;
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
import com.sparta.delivery.backend.store.menu.dto.ReqSortOrderOwnerDto;
import com.sparta.delivery.backend.store.menu.dto.ReqStoreMenuOwnerDto;
import com.sparta.delivery.backend.store.menu.dto.ReqVisibilityOwnerDto;
import com.sparta.delivery.backend.store.menu.dto.ResSortOrderOwnerDto;
import com.sparta.delivery.backend.store.menu.dto.ResStoreMenuOwnerDto;
import com.sparta.delivery.backend.store.menu.dto.ResVisibilityOwnerDto;
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

	// 생성
	@Transactional
	public ResStoreMenuOwnerDto createStoreMenu(
		UUID storeId,
		ReqStoreMenuOwnerDto reqStoreMenuOwnerDto
	) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Store not found"));

		Image image = Image.builder()
			.imageUrl(reqStoreMenuOwnerDto.getImageUrl())
			.build();
		imageRepository.save(image);

		StoreMenu storeMenu = StoreMenu.builder()
			.reqStoreMenuOwnerDto(reqStoreMenuOwnerDto)
			.store(store)
			.image(image)
			.build();

		storeMenuRepository.save(storeMenu);
		return new ResStoreMenuOwnerDto(storeMenu);
	}

	// 조회
	@Transactional(readOnly = true)
	public ResStoreMenuOwnerDto getStoreMenuByStoreMenuId(
		UUID storeId,
		UUID menuId
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(menuId);

		return new ResStoreMenuOwnerDto(storeMenu);
	}

	@Transactional(readOnly = true)
	public Page<ResStoreMenuOwnerDto> getStoreMenusByStoreId(
		UUID storeId,
		int page,
		int size
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());

		Page<StoreMenu> storeMenuList =
			storeMenuRepository.findAllByStoreId(storeId, pageable);

		return storeMenuList.map(ResStoreMenuOwnerDto::new);
	}

	// 수정
	@Transactional
	public ResStoreMenuOwnerDto updateStoreMenu(
		UUID storeId,
		UUID menuId,
		ReqStoreMenuOwnerDto dto
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(menuId);

		// 일단 이미지 테이블에서 일괄 관리한다고 가정했을때
		Image image = Image.builder()
			.imageUrl(dto.getImageUrl())
			.build();
		imageRepository.save(image);

		storeMenu.setName(dto.getName());
		storeMenu.setPrice(dto.getPrice());
		storeMenu.setDescription(dto.getDescription());
		storeMenu.setPrepTime(dto.getPrepTime());
		storeMenu.setStockStatus(dto.getStockStatus());
		storeMenu.setImage(image);

		return new ResStoreMenuOwnerDto(storeMenu);
	}

	@Transactional
	public ResSortOrderOwnerDto updateSortOrder(
		UUID storeId,
		UUID menuId,
		ReqSortOrderOwnerDto dto
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(menuId);

		storeMenu.setSortOrder(dto.getSortOrder());

		return new ResSortOrderOwnerDto(storeMenu);
	}

	@Transactional
	public ResVisibilityOwnerDto updateVisibility(
		UUID storeId,
		UUID menuId,
		ReqVisibilityOwnerDto dto
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(menuId);

		if (dto.isHidden()) {
			// 체크박스 체크됨 → 현재 시간으로 숨김 처리
			storeMenu.setHiddenAt(Instant.now());
		} else {
			// 체크 해제됨 → null 처리
			storeMenu.setHiddenAt(null);
		}

		return new ResVisibilityOwnerDto(storeMenu);
	}

	// 삭제
	@Transactional
	public void deleteStoreMenu(
		UUID storeId,
		UUID menuId
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(menuId);
		storeMenuRepository.delete(storeMenu);
	}

	// 가게 검증
	private void validateStore(UUID storeId) {
		storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Store not found"));
	}

	// 가게 검색
	private StoreMenu findStoreMenu(UUID menuId) {
		return storeMenuRepository.findById(menuId)
			.orElseThrow(() -> new IllegalArgumentException("StoreMenu not found"));
	}
}
