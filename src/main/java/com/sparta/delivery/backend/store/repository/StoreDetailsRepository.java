package com.sparta.delivery.backend.store.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sparta.delivery.backend.store.entity.StoreDetails;

public interface StoreDetailsRepository extends JpaRepository<StoreDetails, UUID> {
	Optional<StoreDetails> findByStoreId(UUID storeId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE StoreDetails sd SET sd.deletedAt = :deletedAt, sd.deletedBy = :deletedBy WHERE sd.store.id = :storeId")
	void bulkSoftDeleteByStoreId(Long storeId, Long deletedBy, Instant deletedAt);
}
