package com.sparta.delivery.backend.store.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.store.entity.StoreImage;
import com.sparta.delivery.backend.store.entity.StoreImageStatusEnum;

public interface StoreImageRepository extends JpaRepository<StoreImage, UUID> {
	StoreImage findFirstByStoreIdAndStatusOrderByCreatedAtAsc(UUID storeId, StoreImageStatusEnum storeImageStatusEnum);
}
