package com.sparta.delivery.backend.store.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.store.entity.StoreDetails;

public interface StoreDetailsRepository extends JpaRepository<StoreDetails, UUID> {
	Optional<StoreDetails> findByStoreId(UUID storeId);
}
