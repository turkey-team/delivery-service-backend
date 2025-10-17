package com.sparta.delivery.backend.store.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, UUID> {
	List<Store> findByOwner(Owner owner);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Store s SET s.deletedAt = :deletedAt, s.deletedBy = :deletedBy WHERE s.id = :storeId")
	void bulkSoftDeleteById(Long storeId, Long deletedBy, Instant deletedAt);

	Optional<Store> findByIdAndDeletedAtIsNull(UUID id);

	@Query("""
		SELECT DISTINCT s FROM Store s
		JOIN s.storeCategories sc
		JOIN sc.category c
		WHERE s.deletedAt IS NULL
		AND ST_Within(:location, s.deliveryZone)
		AND c.id = :categoryId
		""")
	Page<Store> findStoresByCategoryWithinDeliveryZone(
		@Param("location") Point location,
		@Param("categoryId") UUID categoryId,
		Pageable pageable
	);

	@Query("""
		    SELECT DISTINCT s FROM Store s
		    LEFT JOIN s.storeMenus m ON m.deletedAt IS NULL
		    WHERE s.deletedAt IS NULL
		      AND ST_Within(:location, s.deliveryZone)
		      AND (
		           s.name LIKE CONCAT('%', :keyword, '%')
		        OR m.name LIKE CONCAT('%', :keyword, '%')
		      )
		""")
	Page<Store> findStoresByKeywordWithinDeliveryZone(
		@Param("location") Point location,
		@Param("keyword") String keyword,
		Pageable pageable
	);
}
