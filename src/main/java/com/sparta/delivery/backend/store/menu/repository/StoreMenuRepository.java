package com.sparta.delivery.backend.store.menu.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

@Repository
public interface StoreMenuRepository extends JpaRepository<StoreMenu, UUID> {
	
	// 관리자가 관리할 때 Soft Delete 한 메뉴들도 조회 가능해야 함
	Page<StoreMenu> findAllByStoreId(UUID storeId, Pageable pageable);

	// 삭제한 StoreMenu 들은 조회할 필요 없음
	@Query("SELECT m FROM StoreMenu m WHERE m.store.id = :storeId AND m.deletedAt IS NULL")
	Page<StoreMenu> findAllByStoreIdAndNotDeleted(UUID store_id, Pageable pageable);

	Optional<StoreMenu> findByStoreIdAndId(UUID storeId, UUID menuId);
}
