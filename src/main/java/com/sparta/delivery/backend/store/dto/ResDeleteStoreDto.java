package com.sparta.delivery.backend.store.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ResDeleteStoreDto", description = "가게 삭제 응답 DTO")
public class ResDeleteStoreDto {
	@Schema(description = "삭제된 가게 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
	private UUID storeId;
	@Schema(description = "삭제된 가게명", example = "김밥천국")
	private String storeName;
	@Schema(description = "삭제된 가게 사업자번호", example = "1234567890")
	private String businessNumber;

	@Builder
	public ResDeleteStoreDto(UUID storeId, String storeName, String businessNumber) {
		this.storeId = storeId;
		this.storeName = storeName;
		this.businessNumber = businessNumber;
	}
}
