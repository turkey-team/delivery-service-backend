package com.sparta.delivery.backend.store.menu.service;

import java.util.List;
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

		Integer maxSortOrder = storeMenuRepository.findMaxSortOrderByStore(storeId);
		int nextSortOrder = (maxSortOrder == null ? 0 : maxSortOrder) + 1;

		StoreMenu storeMenu = StoreMenu.builder()
			.reqCreateStoreMenuDto(reqCreateStoreMenuDto)
			.store(store)
			.image(image)
			.build();

		// 순서 맨아래로 보내서 자동 재정렬
		storeMenu.setSortOrder(nextSortOrder);

		storeMenuRepository.save(storeMenu);
	}

	// 조회
	// 단일 메뉴 조회 (세부)
	@Transactional(readOnly = true)
	public ResStoreMenuDto getStoreMenuByStoreMenuId(
		UUID storeId,
		UUID menuId
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(storeId, menuId);
		return new ResStoreMenuDto(storeMenu);
	}

	// 전체 메뉴 조회 (페이징)
	@Transactional(readOnly = true)
	public Page<ResStoreMenuDto> getStoreMenusByStoreId(
		UUID storeId,
		int page,
		int size
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());

		Page<StoreMenu> storeMenuList =
			storeMenuRepository.findAllByStoreIdAndDeletedAtIsNull(storeId, pageable, null);

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
		StoreMenu storeMenu = findStoreMenu(storeId, menuId);

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

		// 이동 대상 메뉴
		StoreMenu targetMenu = findStoreMenu(storeId, menuId);
		int targetSortOrder = reqUpdateSortOrderDto.getSortOrder();

		/*
		 우선 변경하려는 메뉴의 sortOrder 를 가장 큰 sortOrder 값의 +100 (임의의 큰 값) 로 shift 시킨다.
		 (이후 다른 수들과 unique 제약이 깨지지 않게 하기 위해서)
		 targetMenu 제외, soft delete 되지 않은 메뉴 중 targetSortOrder 이상인 메뉴들은 +1
		 즉, 내가 2 번째로 sortOrder 를 변경하겠다고 요청하면
		 기존에 있던 sortOrder 가 2이상인 값들은 전부 +1 처리 (3, 4, 5 ...)
		 그리고 이후에 sortOrder 를 2 로 지정.
		 이렇게하면 모든 값들의 unique 를 지킬 수 있으면서도 오름차순으로 정렬할 수 있게 된다.
		*/
		Integer maxSortOrder = storeMenuRepository.findMaxSortOrderByStore(storeId);
		int tempSortOrder = (maxSortOrder == null ? 0 : maxSortOrder) + 100;
		targetMenu.setSortOrder(tempSortOrder);

		List<StoreMenu> menusToShift =
			storeMenuRepository.findAllByStoreIdAndSortOrderGreaterThanEqualAndDeletedAtIsNull(storeId, targetSortOrder);

		for (StoreMenu menu : menusToShift) {
			// 대상 메뉴는 제외
			if (!menu.getId().equals(menuId)) {
				menu.setSortOrder(menu.getSortOrder() + 1);
			}
		}

		// 대상 메뉴 순서 변경
		targetMenu.setSortOrder(targetSortOrder);

		return new ResUpdateSortOrderDto(targetMenu);
	}

	@Transactional
	public ResUpdateVisibilityDto updateVisibility(
		UUID storeId,
		UUID menuId,
		ReqUpdateVisibilityDto reqUpdateVisibilityDto
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(storeId, menuId);

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
		StoreMenu storeMenu = findStoreMenu(storeId, menuId);

		// 현재 로그인 중인 username 가져오기
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		// username 으로 userId 조회
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new RuntimeException("User not found"));

		storeMenu.softDelete(user.getId());

		// 남은 메뉴들 순서 재정렬
		reorderSortOrder(storeId);
	}

	/** 삭제 시 재정렬 **/
	// 메뉴 삭제 시 모든 메뉴 순서 1부터 오름차순 재배치
	private void reorderSortOrder(UUID storeId) {
		List<StoreMenu> menus = storeMenuRepository.findAllByStoreIdOrderBySortAsc(storeId);

		int order = 1;
		for (StoreMenu menu : menus) {
			menu.setSortOrder(order++);
		}
	}

	/** --------------------- Helper --------------------- **/
	// 가게 검증
	private void validateStore(UUID storeId) {
		storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Store not found"));
	}

	// 가게 메뉴 검색 (삭제된 메뉴 제외)
	private StoreMenu findStoreMenu(UUID storeId, UUID menuId) {
		return storeMenuRepository.findByStoreIdAndDeletedAtIsNull(storeId, menuId, null)
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
