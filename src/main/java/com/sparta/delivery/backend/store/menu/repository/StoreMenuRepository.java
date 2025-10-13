package com.sparta.delivery.backend.store.menu.repository;

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
	  (deleteAt, deletedBy 존재 or sortOrder가 음수)
	  도 조회 가능해야 함
	  이때의 정렬 기준은 createAt 기준이므로 sortOrder 는 고려하지 않아도 된다.
	 */
	// Manager: 삭제했던 StoreMenu 들도 조회 필요
	Page<StoreMenu> findAllByStoreId(UUID storeId, Pageable pageable);

	// Owner: 삭제한 StoreMenu 들은 조회할 필요 없음
	Page<StoreMenu> findAllByStoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable);
}
