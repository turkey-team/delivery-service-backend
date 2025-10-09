package com.sparta.delivery.backend.store.menu.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

public interface StoreMenuRepositoryCustom {
	Page<StoreMenu> findAllByStoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable, Instant isDeleted);

	Optional<StoreMenu> findByStoreIdAndDeletedAtIsNull(UUID storeId, UUID menuId, Instant isDeleted);

	Integer findMaxSortOrderByStore(UUID storeId);

	List<StoreMenu> findAllByStoreIdOrderBySortAsc(UUID storeId);
}
