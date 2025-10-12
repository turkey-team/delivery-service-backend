package com.sparta.delivery.backend.store.menu.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.common.LoginUserAuditorAware;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreMenuService {

	private final StoreMenuRepository storeMenuRepository;
	private final StoreRepository storeRepository;
	private final ImageRepository imageRepository;
	private final LoginUserAuditorAware loginUserAuditorAware;

	/** 생성 **/
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

	/** 조회 **/
	// 단일 메뉴 조회 (세부)
	@Transactional(readOnly = true)
	public ResGetStoreMenuDto getStoreMenuByStoreMenuId(
		UUID storeId,
		UUID menuId
	) {
		validateStore(storeId);
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
		Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());

		Page<StoreMenu> storeMenuList =
			storeMenuRepository.findAllByStoreIdAndDeletedAtIsNull(storeId, pageable);

		return storeMenuList.map(ResGetListStoreMenuDto::new);
	}

	/** 수정 **/
	@Transactional
	public void updateStoreMenu(
		UUID storeId,
		UUID menuId,
		ReqUpdateStoreMenuDto reqUpdateStoreMenuDto
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(storeId, menuId);

		// 일단 이미지 테이블에서 일괄 관리한다고 가정했을때
		Image image = saveImage(reqUpdateStoreMenuDto.getImageUrl());

		storeMenu.updateStoreMenu(reqUpdateStoreMenuDto, image);
	}

	@Transactional
	public void updateSortOrder(
		UUID storeId,
		UUID menuId,
		ReqUpdateSortOrderDto reqUpdateSortOrderDto
	) {
		validateStore(storeId);

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
		UUID storeId,
		UUID menuId,
		ReqUpdateVisibilityDto reqUpdateVisibilityDto
	) {
		validateStore(storeId);
		StoreMenu storeMenu = findStoreMenu(storeId, menuId);

		storeMenu.setHiddenAt(reqUpdateVisibilityDto.isHidden());
	}

	/** 삭제 **/
	@Transactional
	public void deleteStoreMenu(UUID storeId, UUID menuId) {
		StoreMenu storeMenu = findStoreMenu(storeId, menuId);

		// 현재 로그인 중인 username 가져오기
		Long userId = loginUserAuditorAware.getCurrentAuditor()
			.orElseThrow(() -> new RuntimeException("Current user not found"));

		storeMenu.softDelete(userId);

		// 남은 메뉴들 순서 재정렬
		reorderSortOrder(storeId);
	}



	/** 삭제 시 재정렬 **/
	// 메뉴 삭제 시 모든 메뉴 순서 1부터 오름차순 재배치
	private void reorderSortOrder(UUID storeId) {
		List<StoreMenu> menus = storeMenuRepository.findAllByStoreIdAndSortOrderGreaterThanEqualAndDeletedAtIsNull(storeId, 1);

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
