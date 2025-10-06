package com.sparta.delivery.backend.store.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.delivery.backend.store.dto.ResGetListStoreDto;

public interface StoreRepositoryCustom {
	Page<ResGetListStoreDto> getStores(Pageable pageable, String sort, String keyword, UUID categoryId);
}
