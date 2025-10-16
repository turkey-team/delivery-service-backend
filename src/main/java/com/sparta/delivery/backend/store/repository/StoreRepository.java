package com.sparta.delivery.backend.store.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, UUID>, StoreRepositoryCustom {
	List<Store> findByOwner(Owner owner);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Store s SET s.deletedAt = :deletedAt, s.deletedBy = :deletedBy WHERE s.id = :storeId")
	void bulkSoftDeleteById(Long storeId, Long deletedBy, Instant deletedAt);

	Optional<Store> findByIdAndDeletedAtIsNull(UUID id);
}
