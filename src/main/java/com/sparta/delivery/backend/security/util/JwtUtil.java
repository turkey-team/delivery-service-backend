package com.sparta.delivery.backend.security.util;

import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtUtil {
	// Header KEY 값
	public static final String AUTHORIZATION_HEADER = "Authorization";
	// Cookie KEY 값
	public static final String REFRESH_TOKEN_COOKIE = "refreshToken";

	// 사용자 권한 값의 KEY
	public static final String AUTHORIZATION_KEY = "auth";
	// Token 식별자
	public static final String BEARER_PREFIX = "Bearer ";

	@Value("${jwt.access.secret}")
	private String accessSecret;

	@Value("${jwt.access.expiration}")
	private Duration accessExpiration;

	@Value("${jwt.refresh.secret}")
	private String refreshSecret;

	@Value("${jwt.refresh.expiration}")
	private Duration refreshExpiration;

	private Key accessKey;
	private Key refreshKey;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

	@PostConstruct
	public void init() {
		byte[] accessBytes = Base64.getDecoder().decode(accessSecret);
		byte[] refreshBytes = Base64.getDecoder().decode(refreshSecret);
		accessKey = Keys.hmacShaKeyFor(accessBytes);
		refreshKey = Keys.hmacShaKeyFor(refreshBytes);
	}

	// Access 토큰 생성
	public String createAccessToken(String username, UserRoleEnum role) {
		Date date = new Date();

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(username)
				.claim(AUTHORIZATION_KEY, role)
				.setExpiration(new Date(date.getTime() + accessExpiration.toMillis()))
				.setIssuedAt(date)
				.signWith(accessKey, signatureAlgorithm)
				.compact();
	}

	// Refresh 토큰 생성
	public String createRefreshToken(String username) {
		Date date = new Date();

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(username)
				.setExpiration(new Date(date.getTime() + refreshExpiration.toMillis()))
				.setIssuedAt(date)
				.signWith(refreshKey, signatureAlgorithm)
				.compact();
	}

	// JWT를 Header에 추가
	public void addAccessTokenToHeader(String accessToken, HttpServletResponse res) {
		res.setHeader(AUTHORIZATION_HEADER, accessToken);
	}

	// JWT를 Header에 추가
	public void addRefreshTokenToCookie(String refreshToken, HttpServletResponse res) {
		// Refresh Token은 HttpOnly Cookie
		Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
		refreshCookie.setHttpOnly(true);  // JS 접근 차단
		//TODO: 추후 보안을 위해 HTTPS만 가능하도록 수정
		refreshCookie.setSecure(false);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge((int) refreshExpiration.toSeconds());
		res.addCookie(refreshCookie);
	}

	// JWT 토큰 substring
	public String substringToken(String tokenValue) {
		if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
			return tokenValue.substring(7);
		}
		logger.error("Not Found Token");
		throw new NullPointerException("Not Found Token");
	}

	// Access 토큰 검증
	public boolean validateAccessToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
		} catch (ExpiredJwtException e) {
			logger.error("Expired JWT token, 만료된 JWT token 입니다.");
		} catch (UnsupportedJwtException e) {
			logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
		return false;
	}

	// Refresh 토큰 검증
	public boolean validateRefreshToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
		} catch (ExpiredJwtException e) {
			logger.error("Expired JWT token, 만료된 JWT token 입니다.");
		} catch (UnsupportedJwtException e) {
			logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
		return false;
	}

	// Access 토큰에서 사용자 정보 가져오기
	public Claims getUserInfoFromAccessToken(String token) {
		return Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token).getBody();
	}

	// Refresh 토큰에서 사용자 정보 가져오기
	public Claims getUserInfoFromRefreshToken(String token) {
		return Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token).getBody();
	}

	// HttpServletRequest에서 Access Token 가져오기
	public String getAccessTokenFromHeader(HttpServletRequest req) {
		String token = req.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(token)) {
			return token;
		}
		return null;
	}

	// Refresh Token 가져오기 (Cookie에서)
	public String getRefreshTokenFromCookie(HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(REFRESH_TOKEN_COOKIE)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
