package com.sparta.delivery.backend.store.menu.service;

import java.util.List;
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
import com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ReqUpdateSortOrderDto;
import com.sparta.delivery.backend.store.menu.dto.ReqUpdateStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ReqUpdateVisibilityDto;
import com.sparta.delivery.backend.store.menu.dto.ResGetListStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ResGetStoreMenuDto;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.repository.StoreMenuRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreMenuService {

	private final StoreMenuRepository storeMenuRepository;
	private final StoreRepository storeRepository;
	private final ImageRepository imageRepository;

	/** 생성 **/
	@Transactional
	public void createStoreMenu(
		User user,
		UUID storeId,
		ReqCreateStoreMenuDto reqCreateStoreMenuDto
	) {


		Store store = validatePermission(user, storeId);
		validateDuplicateMenuName(storeId, reqCreateStoreMenuDto.getName());

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

	/** 조회 **/
	// 단일 메뉴 조회 (세부)
	@Transactional(readOnly = true)
	public ResGetStoreMenuDto getStoreMenuByStoreMenuId(
		UUID storeId,
		UUID menuId
	) {
		storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Store not found"));

		StoreMenu storeMenu = findStoreMenu(storeId, menuId);
		return new ResGetStoreMenuDto(storeMenu);
	}

	// 전체 메뉴 조회 (페이징)
	@Transactional(readOnly = true)
	public Page<ResGetListStoreMenuDto> getStoreMenusByStoreId(
		UUID storeId,
		int page,
		int size
	) {
		storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Store not found"));

		Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());

		Page<StoreMenu> storeMenuList =
			storeMenuRepository.findAllByStoreIdAndDeletedAtIsNull(storeId, pageable);

		// 메뉴가 하나도 없어도 빈 페이지는 반환
		if (storeMenuList == null || storeMenuList.isEmpty()) {
			return Page.empty(pageable);
		}

		return storeMenuList.map(ResGetListStoreMenuDto::new);
	}

	/** 수정 **/
	@Transactional
	public void updateStoreMenu(
		User user,
		UUID storeId,
		UUID menuId,
		ReqUpdateStoreMenuDto reqUpdateStoreMenuDto
	) {
		validatePermission(user, storeId);
		validateDuplicateMenuName(storeId, reqUpdateStoreMenuDto.getName());

		StoreMenu storeMenu = findStoreMenu(storeId, menuId);

		Image image = saveImage(reqUpdateStoreMenuDto.getImageUrl());

		storeMenu.updateStoreMenu(reqUpdateStoreMenuDto, image);
	}

	@Transactional
	public void updateSortOrder(
		User user,
		UUID storeId,
		UUID menuId,
		ReqUpdateSortOrderDto reqUpdateSortOrderDto
	) {
		validatePermission(user, storeId);

		// 이동 대상 메뉴
		StoreMenu targetMenu = findStoreMenu(storeId, menuId);
		int targetSortOrder = reqUpdateSortOrderDto.getSortOrder();

		/*
		 우선 이동하고싶은 sortOrder 보다 크거나 같은 값들을 +100 (임의의 큰 값) 으로 임시 shift 시킨다.
		 (다른 sortOrder 들과 unique 제약이 깨지지 않게)
		 그리고 이후에 sortOrder 를 이동하고싶은 숫자로 set 한 이후에
		 임시 shift 했던 값들을 다시 재정렬 시켜주는 과정을 거치면,
		 모든 값들의 unique 를 지킬 수 있으면서도 오름차순으로 정렬할 수 있게 된다.
		*/

		List<StoreMenu> menusToShift =
			storeMenuRepository.findAllByStoreIdAndSortOrderGreaterThanEqualAndDeletedAtIsNull(
				storeId, targetSortOrder
			);

		for (StoreMenu menu : menusToShift) {
			// 대상 메뉴는 제외
			if (!menu.getId().equals(menuId)) {
				menu.setSortOrder(menu.getSortOrder() + 100); // 임의의 큰 값
			}
		}
		storeMenuRepository.flush(); // DB 반영 → unique 충돌 방지

		// 타겟 메뉴를 요청된 위치에 세팅
		targetMenu.setSortOrder(targetSortOrder);
		storeMenuRepository.flush();

		// 전체 메뉴 순서 재정렬
		reorderSortOrder(storeId);
	}

	@Transactional
	public void updateVisibility(
		User user,
		UUID storeId,
		UUID menuId,
		ReqUpdateVisibilityDto reqUpdateVisibilityDto
	) {
		validatePermission(user, storeId);
		StoreMenu storeMenu = findStoreMenu(storeId, menuId);

		storeMenu.setHiddenAt(reqUpdateVisibilityDto.isHidden());
	}

	/** 삭제 **/
	@Transactional
	public void deleteStoreMenu(User user, UUID storeId, UUID menuId) {
		validatePermission(user, storeId);
		StoreMenu storeMenu = findStoreMenu(storeId, menuId);

		// sortOrder 중 가장 작은 수 - 1로 지정, 즉 삭제된 메뉴들이 음수 sortOrder 값의 내림차순으로 쌓이는 과정
		Integer minSortOrder = storeMenuRepository.findMinSortOrderByStore(storeId);
		if (minSortOrder == null) minSortOrder = 0;

		storeMenu.softDelete(user.getPublicId(), minSortOrder);
		// 남은 메뉴들 순서 재정렬
		reorderSortOrder(storeId);
	}

	/** 삭제 시 재정렬 **/
	// 메뉴 삭제 시 모든 메뉴 순서 1부터 오름차순 재배치
	private void reorderSortOrder(UUID storeId) {
		List<StoreMenu> menus = storeMenuRepository.findAllByStoreIdAndSortOrderGreaterThanEqualAndDeletedAtIsNull(storeId, 1);

		int order = 1;
		for (StoreMenu menu : menus) {
			if (menu.getSortOrder() >= 100) continue; // 임시 이동된 메뉴 제외하게끔
			menu.setSortOrder(order++);
		}

		// 100 단위로 밀려있던 메뉴들 정렬 복원
		for (StoreMenu menu : menus) {
			if (menu.getSortOrder() >= 100) {
				menu.setSortOrder(order++);
			}
		}
	}

	/** --------------------- Helper --------------------- **/
	// 메뉴명 중복 검사
	private void validateDuplicateMenuName(UUID storeId, String menuName) {
		if (storeMenuRepository.findByStoreIdAndName(storeId, menuName).isPresent()) {
			throw new IllegalArgumentException("Menu name already exists");
		}
	}

	// 권한 검증
	private Store validatePermission(User user, UUID storeId) {
		// 가게 유효성 검증
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Store not found"));

		// Store의 Owner 인 경우
		if (store.getOwner().getUser().getPublicId().equals(user.getPublicId())) return store;

		// Manager or Master 권한 허용
		if (user.getRole() == UserRoleEnum.MANAGER || user.getRole() == UserRoleEnum.MASTER) return store;

		throw new SecurityException("You do not have permission");
	}

	// 가게 메뉴 검색 (삭제된 메뉴 제외)
	private StoreMenu findStoreMenu(UUID storeId, UUID menuId) {
		return storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null)
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
