package com.sparta.delivery.backend.store.dto;

import java.util.UUID;

import com.sparta.delivery.backend.store.entity.StoreStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ResUpdateStoreStatusDto", description = "가게 상태 수정 응답 DTO")
public class ResUpdateStoreStatusDto {
	@Schema(description = "수정한 가게 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
	private UUID storeId;
	@Schema(description = "수정된 가게 상태", example = "CLOSED")
	private StoreStatusEnum storeStatus;

	@Builder
	public ResUpdateStoreStatusDto(UUID storeId, StoreStatusEnum storeStatus) {
		this.storeId = storeId;
		this.storeStatus = storeStatus;
	}
}
