package com.sparta.delivery.backend.address.dto;

import java.util.UUID;

import com.sparta.delivery.backend.address.entity.Address;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResAddressDto {
	private UUID addressId;
	private String address;
	private boolean isDefault;

	public static ResAddressDto from(Address address) {
		return ResAddressDto.builder()
			.addressId(address.getId())
			.address(address.getAddress())
			.isDefault(address.getIsDefault())
			.build();
	}
}
