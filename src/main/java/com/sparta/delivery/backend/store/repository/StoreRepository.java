package com.sparta.delivery.backend.store.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, UUID>, StoreRepositoryCustom {
	List<Store> findByOwner(Owner owner);
}
