package com.sparta.delivery.backend.store.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sparta.delivery.backend.store.entity.StoreImage;
import com.sparta.delivery.backend.store.entity.StoreImageStatusEnum;

public interface StoreImageRepository extends JpaRepository<StoreImage, UUID> {
	Optional<StoreImage> findFirstByStoreIdAndStatusAndDeletedAtIsNullOrderByCreatedAtAsc(UUID storeId, StoreImageStatusEnum storeImageStatusEnum);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("UPDATE StoreImage si SET si.deletedAt = :deletedAt, si.deletedBy = :deletedBy WHERE si.store.id = :storeId")
	void bulkSoftDeleteByStoreId(UUID storeId, Long deletedBy, Instant deletedAt);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Image i SET i.deletedAt = :deletedAt, i.deletedBy = :deletedBy " +
		"WHERE i.id IN (SELECT si.image.id FROM StoreImage si WHERE si.store.id = :storeId AND si.image IS NOT NULL)")
	void bulkSoftDeleteImageByStoreId(Long storeId, Long deletedBy, Instant deletedAt);

}
