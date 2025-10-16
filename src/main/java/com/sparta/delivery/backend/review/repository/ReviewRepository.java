package com.sparta.delivery.backend.review.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sparta.delivery.backend.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepoistoryCustom {

	Optional<Review> findByIdAndDeletedAtIsNull(UUID reviewId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Review r SET r.deletedAt = :deletedAt, r.deletedBy = :deletedBy WHERE r.store.id = :storeId")
	void bulkSoftDeleteByStoreId(Long storeId, Long deletedBy, Instant deletedAt);
}