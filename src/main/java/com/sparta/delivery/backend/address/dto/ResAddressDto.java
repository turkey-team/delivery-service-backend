package com.sparta.delivery.backend.address.dto;

import java.util.UUID;

import com.sparta.delivery.backend.address.entity.Address;

import lombok.Getter;

@Getter
public class ResAddressDto {
	private UUID addressId;
	private String address;

	public ResAddressDto(UUID addressId, String address) {
		this.addressId = addressId;
		this.address = address;
	}

	public static ResAddressDto from(Address address) {
		return new ResAddressDto(address.getId(), address.getAddress());
	}
}
