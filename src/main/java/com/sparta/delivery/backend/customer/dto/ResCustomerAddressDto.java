package com.sparta.delivery.backend.customer.dto;

import java.util.UUID;

import com.sparta.delivery.backend.customer.entity.CustomerAddress;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResCustomerAddressDto {

	@Schema(description = "고객 배송지 ID", example = "ea977eb1-5aff-4f51-a931-1ce8e26a8d92")
	private UUID customerAddressId;

	@Schema(description = "주소 ID", example = "fa977eb1-5aff-4f51-a931-1ce8e26a8d93")
	private UUID addressId;

	@Schema(description = "지역 코드", example = "103")
	private String regionCode;

	@Schema(description = "전체 주소", example = "서울특별시 종로구 돈화문로 27")
	private String fullAddress;

	@Schema(description = "기본 배송지 여부", example = "true")
	private Boolean isDefault;

	@Schema(description = "배송지 별명", example = "우리집")
	private String nickname;

	public static ResCustomerAddressDto from(CustomerAddress customerAddress) {
		return ResCustomerAddressDto.builder()
			.customerAddressId(customerAddress.getId())
			.addressId(customerAddress.getAddress().getId())
			.regionCode(customerAddress.getDongCode())
			.fullAddress(customerAddress.getAddress().getFullAddress())
			.isDefault(customerAddress.getIsDefault())
			.nickname(customerAddress.getNickname())
			.build();
	}
}
