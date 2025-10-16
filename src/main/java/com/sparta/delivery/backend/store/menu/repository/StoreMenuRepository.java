package com.sparta.delivery.backend.store.menu.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

import jakarta.validation.constraints.NotNull;

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

	Page<StoreMenu> findAllByStoreIdAndDeletedAtIsNullAndHiddenAtIsNull(UUID storeId, Pageable pageable);

	Optional<StoreMenu> findByStoreIdAndName(UUID storeId, String name);


	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE StoreMenu sm SET sm.deletedAt = :deletedAt, sm.deletedBy = :deletedBy WHERE sm.store.id = :storeId")
	void bulkSoftDeleteByStoreId(Long storeId, Long deletedBy, Instant deletedAt);

	Optional<StoreMenu> findByIdAndDeletedAtIsNull(UUID menuId);
}
