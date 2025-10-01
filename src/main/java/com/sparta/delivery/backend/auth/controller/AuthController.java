package com.sparta.delivery.backend.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.auth.dto.LogoutRequestDto;
import com.sparta.delivery.backend.auth.dto.RefreshTokenRequestDto;
import com.sparta.delivery.backend.auth.dto.RefreshTokenResponseDto;
import com.sparta.delivery.backend.auth.service.AuthService;
import com.sparta.delivery.backend.security.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final JwtUtil jwtUtil;

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);

		if (refreshToken != null) {
			LogoutRequestDto requestDto = new LogoutRequestDto(jwtUtil.substringToken(refreshToken));
			authService.logout(requestDto);
		}
		// Cookie 삭제
		jwtUtil.deleteCookieFromResponse(response);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/refresh")
	public ResponseEntity<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);
		RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto(jwtUtil.substringToken(refreshToken));
		RefreshTokenResponseDto responseDto = authService.refreshToken(requestDto);

		// 새 액세스 토큰 전달
		response.setHeader(JwtUtil.AUTHORIZATION_HEADER, responseDto.getAccessToken());

		// 새 리프레시 토큰 전달
		jwtUtil.addRefreshTokenToCookie(responseDto.getRefreshToken(), response);

		return ResponseEntity.ok().build();
	}
}
