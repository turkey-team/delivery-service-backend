package com.sparta.delivery.backend.store.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.store.entity.StoreCategory;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, UUID> {
}
