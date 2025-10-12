package com.sparta.delivery.backend.order.dto;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.customer.entity.Customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResCheckOutOrderDto {
	private String sidoName;        // 시/도
	private String sigunguName;     // 시/군/구
	private String dongName;        // 동
	private String addressDetail;   // 상세 주소
	private String phoneNumber;     // 고객 연락처
	private int menusPrice;         // 메뉴 합계
	private int deliveryFee;        // 배달비

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
