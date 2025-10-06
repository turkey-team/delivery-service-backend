package com.sparta.delivery.backend.customer.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.customer.entity.Customer;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResGetCustomerDto {
	private String username;
	private String nickname;
	private String email;
	private String phoneNumber;
	private Instant createdAt;
	private Instant updatedAt;

	public static ResGetCustomerDto from(Customer customer) {
		return ResGetCustomerDto.builder()
			.username(customer.getUser().getUsername())
			.nickname(customer.getNickname())
			.email(customer.getEmail())
			.phoneNumber(customer.getPhoneNumber())
			.createdAt(customer.getCreatedAt())
			.updatedAt(customer.getUpdatedAt())
			.build();
	}
}
