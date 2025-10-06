package com.sparta.delivery.backend.store.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResUpdateStoreInfoDto {
	//store
	private UUID storeId;
	private String storeName;
	private String addressDetails;
	private String phoneNumber;
	private String regionDong;

	@Builder
	public ResUpdateStoreInfoDto(UUID storeId, String storeName, String addressDetails, String phoneNumber, String regionDong, String businessNumber) {
		this.storeId = storeId;
		this.storeName = storeName;
		this.addressDetails = addressDetails;
		this.phoneNumber = phoneNumber;
		this.regionDong = regionDong;
	}
}
