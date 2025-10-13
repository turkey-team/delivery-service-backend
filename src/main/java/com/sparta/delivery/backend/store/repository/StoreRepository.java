package com.sparta.delivery.backend.store.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, UUID>, StoreRepositoryCustom {
}
