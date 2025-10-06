package com.sparta.delivery.backend.store.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.sparta.delivery.backend.store.entity.Store;

public interface StoreRepository extends CrudRepository<Store, UUID>, StoreRepositoryCustom {
}
