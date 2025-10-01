package com.sparta.delivery.backend.store.menu.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

@Repository
public interface StoreMenuRepository extends JpaRepository<StoreMenu, Long> {
	Page<StoreMenu> findAllByStoreId(UUID store_id, Pageable pageable);
}
