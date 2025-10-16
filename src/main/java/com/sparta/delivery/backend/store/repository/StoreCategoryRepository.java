package com.sparta.delivery.backend.store.repository;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sparta.delivery.backend.store.entity.StoreCategory;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, UUID> {
	boolean existsByCategoryIdAndDeletedAtIsNull(UUID categoryId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE StoreCategory sc SET sc.deletedAt = :deletedAt, sc.deletedBy = :deletedBy WHERE sc.store.id = :storeId")
	void bulkSoftDeleteByStoreId(Long storeId, Long deletedBy, Instant deletedAt);
}
