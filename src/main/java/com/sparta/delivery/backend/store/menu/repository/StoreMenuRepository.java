package com.sparta.delivery.backend.store.menu.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

@Repository
public interface StoreMenuRepository extends JpaRepository<StoreMenu, UUID>, StoreMenuRepositoryCustom {
	
	/*
	 TODO:
	  관리자가 조회할 때, Soft Delete한 메뉴들
	  (deleteAt, deletedBy 존재 + sortOrder가 음수)
	  도 조회 가능해야 함
	 */
	Page<StoreMenu> findAllByStoreId(UUID storeId, Pageable pageable);

	// 삭제한 StoreMenu 들은 조회할 필요 없음
	Page<StoreMenu> findAllByStoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable, Instant deletedAt);

	Optional<StoreMenu> findByStoreIdAndDeletedAtIsNull(UUID storeId, UUID menuId, Instant deletedAt);

	List<StoreMenu> findAllByStoreIdAndSortOrderGreaterThanEqualAndDeletedAtIsNull(UUID storeId, int sortOrder);
}
