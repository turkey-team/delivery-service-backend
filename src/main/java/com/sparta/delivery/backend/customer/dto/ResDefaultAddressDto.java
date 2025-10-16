package com.sparta.delivery.backend.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResDefaultAddressDto {

	@Schema(description = "기본 배송지 설정 여부")
	private boolean hasDefaultAddress;

	@Schema(description = "기본 배송지 정보")
	private ResCustomerAddressDto address;

	public static ResDefaultAddressDto empty() {
		return new ResDefaultAddressDto(false, null);
	}

	public static ResDefaultAddressDto of(ResCustomerAddressDto address) {
		return new ResDefaultAddressDto(true, address);
	}
}
