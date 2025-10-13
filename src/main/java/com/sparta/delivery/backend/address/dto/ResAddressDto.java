package com.sparta.delivery.backend.address.dto;

import java.util.UUID;

import com.sparta.delivery.backend.address.entity.Address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResAddressDto {

	@Schema(description = "주소 ID", example = "ea977eb1-5aff-4f51-a931-1ce8e26a8d92")
	private UUID addressId;

	@Schema(description = "주소", example = "서울특별시 마포구 망원1동 마포나루길 4")
	private String address;

	@Schema(description = "기본 주소 여부", example = "true or false")
	private boolean isDefault;

	public static ResAddressDto from(Address address) {
		return ResAddressDto.builder()
			.addressId(address.getId())
			.address(address.getAddress())
			.isDefault(address.getIsDefault())
			.build();
	}
}
