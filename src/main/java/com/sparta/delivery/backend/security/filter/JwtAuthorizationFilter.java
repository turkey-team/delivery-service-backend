package com.sparta.delivery.backend.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sparta.delivery.backend.security.UserDetailsServiceImpl;
import com.sparta.delivery.backend.security.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;

	public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws
		ServletException, IOException {

		String tokenValue = jwtUtil.getAccessTokenFromHeader(req);

		if (StringUtils.hasText(tokenValue)) {
			// JWT 토큰 substring
			tokenValue = jwtUtil.substringToken(tokenValue);
			log.info(tokenValue);

			if (!jwtUtil.validateAccessToken(tokenValue)) {
				log.error("Token Error");
				res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 설정
				res.getWriter().write("Invalid token");
				return;
			}

			Claims info = jwtUtil.getUserInfoFromAccessToken(tokenValue);

			try {
				setAuthentication(info.getSubject());
			} catch (Exception e) {
				log.error("setAuthentication error : " + e.getMessage());
				res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 설정
				res.getWriter().write("Authentication failed");
				return;
			}
		}

		filterChain.doFilter(req, res);
	}

	// 인증 처리
	public void setAuthentication(String username) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = createAuthentication(username);
		context.setAuthentication(authentication);

		SecurityContextHolder.setContext(context);
	}

	// 인증 객체 생성
	private Authentication createAuthentication(String username) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
}