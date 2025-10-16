package com.sparta.delivery.backend.store.dto;

import java.util.List;
import java.util.UUID;

import com.sparta.delivery.backend.address.entity.Address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ResUpdateStoreInfoDto", description = "가게 정보 수정 응답 DTO")
public class ResUpdateStoreInfoDto {
	//store
	@Schema(description = "가게 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
	private UUID storeId;
	@Schema(description = "가게 이름", example = "김밥천국")
	private String storeName;
	@Schema(description = "가게 주소", example = "광화문로 1길 1")
	private String fullAddress;
	@Schema(description = "가게 연락처", example = "02-1234-5678")
	private String phoneNumber;


	@Builder
	public ResUpdateStoreInfoDto(UUID storeId, String storeName, String fullAddress, String phoneNumber) {
		this.storeId = storeId;
		this.storeName = storeName;
		this.fullAddress = fullAddress;
		this.phoneNumber = phoneNumber;
	}
}
