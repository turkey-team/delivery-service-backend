package com.sparta.delivery.backend.store.dto;

import java.util.List;
import java.util.UUID;

import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreDetails;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResCreateStoreDto {
	private UUID id;
	private String name;

	public ResCreateStoreDto(UUID id, String name) {
		this.id = id;
		this.name = name;
	}

}
