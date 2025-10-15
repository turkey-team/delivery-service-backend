package com.sparta.delivery.backend.order.dto;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.customer.entity.Customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResCheckOutOrderDto {

	@Schema(description = "시/도 이름", example = "고양시")
	private String sidoName;

	@Schema(description = "시/군/구 이름", example = "덕양구")
	private String sigunguName;

	@Schema(description = "동 이름", example = "화정동")
	private String dongName;

	@Schema(description = "상세 주소", example = "백양로 65")
	private String addressDetail;

	@Schema(description = "고객 연락처", example = "010-1234-5678")
	private String phoneNumber;

	@Schema(description = "메뉴 합계", example = "13000")
	private int menusPrice;

	@Schema(description = "배달비", example = "1500")
	private int deliveryFee;

	public static ResCheckOutOrderDto from(Customer customer, Address address, int menusPrice, int deliveryFee) {
		return ResCheckOutOrderDto.builder()
			.sidoName(address.getDong().getSigungu().getSido().getName())  // 시/도
			.sigunguName(address.getDong().getSigungu().getName())         // 시/군/구
			.dongName(address.getDong().getName())                         // 동
			.addressDetail(address.getAddress())                           // 상세주소
			.phoneNumber(customer.getPhoneNumber())                        // 연락처
			.menusPrice(menusPrice)
			.deliveryFee(deliveryFee)
			.build();
	}
}
