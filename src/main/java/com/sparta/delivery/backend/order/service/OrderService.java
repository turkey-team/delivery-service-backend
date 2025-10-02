package com.sparta.delivery.backend.order.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.order.dto.ReqOrderCreateDto;
import com.sparta.delivery.backend.order.dto.ResOrderCreateDto;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final StoreRepository storeRepository;

	@Transactional
	public ResOrderCreateDto createOrder(UUID storeId, ReqOrderCreateDto reqOrderCreateDto) {

		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException("Store not found"));

		return null;
	}
}
