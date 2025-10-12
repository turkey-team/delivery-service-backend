package com.sparta.delivery.backend.store.menu.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

public interface StoreMenuRepositoryCustom {

	// 특정 메뉴 1개 조회
	Optional<StoreMenu> findByStoreIdAndDeletedAtIsNull(UUID storeId, UUID menuId, Instant deletedAt);

	// 정렬 순서가 특정 값 이상인 메뉴 조회 (메뉴 reorder 시 활용)
	List<StoreMenu> findAllByStoreIdAndSortOrderGreaterThanEqualAndDeletedAtIsNull(UUID storeId, int sortOrder);
}
