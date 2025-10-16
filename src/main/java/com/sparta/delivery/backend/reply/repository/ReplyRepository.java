package com.sparta.delivery.backend.reply.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sparta.delivery.backend.reply.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, UUID> {
	List<Reply> findByReviewId(UUID reviewId);

	List<Reply> findAllByReviewIdAndDeletedAtIsNull(UUID reviewId);

	boolean existsByReviewId(UUID reviewId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Reply rp SET rp.deletedAt = :deletedAt, rp.deletedBy = :deletedBy " +
		"WHERE rp.review.id IN (SELECT r.id FROM Review r WHERE r.store.id = :storeId)")
	void bulkSoftDeleteReplyByStoreId(Long storeId, Long deletedBy, Instant deletedAt);
}