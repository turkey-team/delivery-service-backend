package com.sparta.delivery.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenResponseDto {
	private String accessToken;
	private String refreshToken;
}
