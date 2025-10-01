package com.sparta.delivery.backend.auth.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.coyote.Response;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sparta.delivery.backend.auth.dto.LogoutRequestDto;
import com.sparta.delivery.backend.auth.dto.RefreshTokenRequestDto;
import com.sparta.delivery.backend.auth.dto.RefreshTokenResponseDto;
import com.sparta.delivery.backend.global.excpetion.UnauthorizedException;
import com.sparta.delivery.backend.security.util.JwtUtil;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private static final String BLACKLIST_PREFIX_LOGOUT = "BL:LO:";
	private static final String BLACKLIST_PREFIX_ROTATED = "BL:RT:";

	private final PasswordEncoder passwordEncoder;
	private final UserRepository repository;
	private final RedisTemplate<String, String> redisTemplate;
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	public void logout(LogoutRequestDto requestDto) {
		// 로그아웃 블랙리스트 등록
		Claims claims = jwtUtil.getUserInfoFromRefreshToken(requestDto.getRefreshToken());
		addToBlacklist(requestDto.getRefreshToken(), claims, BLACKLIST_PREFIX_LOGOUT);
	}

	public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto requestDto) {
		//검증
		if (!jwtUtil.validateRefreshToken(requestDto.getRefreshToken())) {
			throw new UnauthorizedException("Invalid token");
		}

		// 블랙리스트 체크
		if (Boolean.TRUE.equals(redisTemplate.hasKey(requestDto.getRefreshToken()))) {
			throw new UnauthorizedException("Token is blacklisted");
		}

		Claims claims = jwtUtil.getUserInfoFromRefreshToken(requestDto.getRefreshToken());
		String username = claims.getSubject();

		// User 조회
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new RuntimeException("User not found"));

		// 새 토큰 발급 (Rotation)
		String newAccessToken = jwtUtil.createAccessToken(username, user.getRole());
		String newRefreshToken = jwtUtil.createRefreshToken(username);

		// 기존 토큰 블랙리스트 등록
		addToBlacklist(requestDto.getRefreshToken(), claims, BLACKLIST_PREFIX_ROTATED);

		return new RefreshTokenResponseDto(newAccessToken, newRefreshToken);
	}

	private void addToBlacklist(String refreshToken, Claims claims, String prefix) {
		long expiration = claims.getExpiration().getTime() - System.currentTimeMillis();

		redisTemplate.opsForValue().set(
			prefix + refreshToken,
			claims.getSubject(),
			expiration,
			TimeUnit.MILLISECONDS
		);
	}
}