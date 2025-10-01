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
import com.sparta.delivery.backend.store.menu.dto.ReqSortOrderDto;
import com.sparta.delivery.backend.store.menu.dto.ReqStoreMenuCreateDto;
import com.sparta.delivery.backend.store.menu.dto.ReqStoreMenuUpdateDto;
import com.sparta.delivery.backend.store.menu.dto.ReqVisibilityDto;
import com.sparta.delivery.backend.store.menu.dto.ResSortOrderDto;
import com.sparta.delivery.backend.store.menu.dto.ResStoreMenuCreateDto;
import com.sparta.delivery.backend.store.menu.dto.ResStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ResStoreMenuUpdateDto;
import com.sparta.delivery.backend.store.menu.dto.ResVisibilityDto;
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
	public ResStoreMenuCreateDto createStoreMenu(
		UUID storeId,
		ReqStoreMenuCreateDto reqStoreMenuCreateDto
	) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Store not found"));

		Image image = saveImage(reqStoreMenuCreateDto.getImageUrl());

		StoreMenu storeMenu = StoreMenu.builder()
			.reqStoreMenuCreateDto(reqStoreMenuCreateDto)
			.store(store)
			.image(image)
			.build();

		storeMenuRepository.save(storeMenu);
		return new ResStoreMenuCreateDto(storeMenu);
	}

	// 조회
	@Transactional(readOnly = true)
	public ResStoreMenuDto getStoreMenuByStoreMenuId(
		UUID storeId,
		UUID menuId
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(menuId);

		return new ResStoreMenuDto(storeMenu);
	}

	@Transactional(readOnly = true)
	public Page<ResStoreMenuDto> getStoreMenusByStoreId(
		UUID storeId,
		int page,
		int size
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());

		Page<StoreMenu> storeMenuList =
			storeMenuRepository.findAllByStoreId(storeId, pageable);

		return storeMenuList.map(ResStoreMenuDto::new);
	}

	// 수정
	@Transactional
	public ResStoreMenuUpdateDto updateStoreMenu(
		UUID storeId,
		UUID menuId,
		ReqStoreMenuUpdateDto reqStoreMenuUpdateDto
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(menuId);

		// 일단 이미지 테이블에서 일괄 관리한다고 가정했을때
		Image image = saveImage(reqStoreMenuUpdateDto.getImageUrl());

		storeMenu.updateStoreMenu(reqStoreMenuUpdateDto, image);
		return new ResStoreMenuUpdateDto(storeMenu);
	}

	@Transactional
	public ResSortOrderDto updateSortOrder(
		UUID storeId,
		UUID menuId,
		ReqSortOrderDto reqSortOrderDto
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(menuId);

		storeMenu.setSortOrder(reqSortOrderDto.getSortOrder());

		return new ResSortOrderDto(storeMenu);
	}

	@Transactional
	public ResVisibilityDto updateVisibility(
		UUID storeId,
		UUID menuId,
		ReqVisibilityDto reqVisibilityDto
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(menuId);

		if (reqVisibilityDto.isHidden()) {
			// 체크박스 체크됨 → 현재 시간으로 숨김 처리
			storeMenu.setHiddenAt(true);
		} else {
			// 체크 해제됨 → null 처리
			storeMenu.setHiddenAt(null);
		}

		return new ResVisibilityDto(storeMenu);
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

	// 이미지 저장
	private Image saveImage(String imageUrl) {
		Image image = Image.builder()
			.imageUrl(imageUrl)
			.build();
		imageRepository.save(image);
		return image;
	}
}
