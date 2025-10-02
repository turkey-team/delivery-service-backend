package com.sparta.delivery.backend.store.menu.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ReqUpdateSortOrderDto;
import com.sparta.delivery.backend.store.menu.dto.ReqUpdateStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ReqUpdateVisibilityDto;
import com.sparta.delivery.backend.store.menu.dto.ResStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ResUpdateSortOrderDto;
import com.sparta.delivery.backend.store.menu.dto.ResUpdateVisibilityDto;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.repository.StoreMenuRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreMenuService {

	private final StoreMenuRepository storeMenuRepository;
	private final StoreRepository storeRepository;
	private final ImageRepository imageRepository;
	private final UserRepository userRepository;

	// 생성
	@Transactional
	public void createStoreMenu(
		UUID storeId,
		ReqCreateStoreMenuDto reqCreateStoreMenuDto
	) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Store not found"));

		Image image = saveImage(reqCreateStoreMenuDto.getImageUrl());

		StoreMenu storeMenu = StoreMenu.builder()
			.store(store)
			.image(image)
			.build();

		storeMenuRepository.save(storeMenu);
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
			storeMenuRepository.findAllByStoreIdAndNotDeleted(storeId, pageable);

		return storeMenuList.map(ResStoreMenuDto::new);
	}

	// 수정
	@Transactional
	public ResponseEntity<Void> updateStoreMenu(
		UUID storeId,
		UUID menuId,
		ReqUpdateStoreMenuDto reqUpdateStoreMenuDto
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(menuId);

		// 일단 이미지 테이블에서 일괄 관리한다고 가정했을때
		Image image = saveImage(reqUpdateStoreMenuDto.getImageUrl());

		storeMenu.updateStoreMenu(reqUpdateStoreMenuDto, image);

		return ResponseEntity.noContent().build();
	}

	@Transactional
	public ResUpdateSortOrderDto updateSortOrder(
		UUID storeId,
		UUID menuId,
		ReqUpdateSortOrderDto reqUpdateSortOrderDto
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(menuId);

		storeMenu.setSortOrder(reqUpdateSortOrderDto.getSortOrder());

		return new ResUpdateSortOrderDto(storeMenu);
	}

	@Transactional
	public ResUpdateVisibilityDto updateVisibility(
		UUID storeId,
		UUID menuId,
		ReqUpdateVisibilityDto reqUpdateVisibilityDto
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(menuId);

		if (reqUpdateVisibilityDto.isHidden()) {
			// 체크박스 체크됨 → 현재 시간으로 숨김 처리
			storeMenu.setHiddenAt(true);
		} else {
			// 체크 해제됨 → null 처리
			storeMenu.setHiddenAt(null);
		}

		return new ResUpdateVisibilityDto(storeMenu);
	}

	// 삭제
	@Transactional
	public void deleteStoreMenu(UUID storeId, UUID menuId) {
		StoreMenu storeMenu = storeMenuRepository.findByStoreIdAndId(storeId, menuId)
			.orElseThrow(() -> new RuntimeException("Menu not found"));

		// 현재 로그인 중인 username 가져오기
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		// username 으로 userId 조회
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new RuntimeException("User not found"));

		storeMenu.softDelete(user.getId());
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
