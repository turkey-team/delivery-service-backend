package com.sparta.delivery.backend.customer.dto;

import java.time.Instant;

import com.sparta.delivery.backend.customer.entity.Customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "ResGetMyCustomerDto", description = "고객 마이페이지 조회")
public class ResGetMyCustomerDto {
	@Schema(description = "사용자 아이디", example = "customer1")
	private String username;

	@Schema(description = "닉네임", example = "홍길동")
	private String nickname;

	@Schema(description = "이메일", example = "customer@example.com")
	private String email;

	@Schema(description = "전화번호", example = "010-1234-5678")
	private String phoneNumber;

	@Schema(description = "가입일시", example = "2025-01-15T10:30:00Z")
	private Instant createdAt;

	public static ResGetMyCustomerDto from(Customer customer) {
		return ResGetMyCustomerDto.builder()
			.username(customer.getUsername())
			.nickname(customer.getNickname())
			.email(customer.getEmail())
			.phoneNumber(customer.getFormattedPhoneNumber())
			.createdAt(customer.getCreatedAt())
			.build();
	}
}
