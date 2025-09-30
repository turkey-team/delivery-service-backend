package com.sparta.delivery.backend.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.security.dto.LoginRequestDto;
import com.sparta.delivery.backend.security.util.JwtUtil;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
		setFilterProcessesUrl("/v1/user/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		log.info("로그인 시도");
		try {
			LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

			return getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(
					requestDto.getUsername(),
					requestDto.getPassword(),
					null
				)
			);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
		log.info("로그인 성공 및 JWT 생성");
		String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
		UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

		String accessToken = jwtUtil.createAccessToken(username, role);
		String refreshToken = jwtUtil.createRefreshToken(username);

		jwtUtil.addAccessTokenToHeader(accessToken, response);
		jwtUtil.addRefreshTokenToCookie(refreshToken, response);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
		log.info("로그인 실패");
		response.setStatus(401);
	}
}