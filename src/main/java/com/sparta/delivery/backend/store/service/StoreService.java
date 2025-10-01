package com.sparta.delivery.backend.store.service;

import org.springframework.stereotype.Service;

import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.owner.repository.OwnerRepository;
import com.sparta.delivery.backend.store.dto.StoreRequestDto;
import com.sparta.delivery.backend.store.dto.StoreResponseDto;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.repository.StoreRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final OwnerRepository ownerRepository;
	private final ImageRepository imageRepository;

	public StoreResponseDto createStore(StoreRequestDto requestDto /*, User user*/) {

		// 1. Owner 인지 검증

			// UserRole userRole = user.getAuthorities();
			// 아니면 throw new IllegalArgumentException("가게 생성 권한이 없습니다");

			// Owner owner = OwnerRepository.findByUser_Id(user.getId());

		// 2. 이미지 등록

			// 2-1.image table insert


		// 3. store insert
			// store = new
			// save()

			// 3-2. store-image table insert

		// 4. store_details insert



		//


		return null;
	}
}
