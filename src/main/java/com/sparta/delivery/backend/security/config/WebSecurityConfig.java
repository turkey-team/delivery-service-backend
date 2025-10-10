package com.sparta.delivery.backend.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sparta.delivery.backend.security.UserDetailsServiceImpl;
import com.sparta.delivery.backend.security.filter.JwtAuthenticationFilter;
import com.sparta.delivery.backend.security.filter.JwtAuthorizationFilter;
import com.sparta.delivery.backend.security.util.JwtUtil;

@Configuration
@EnableWebSecurity //완성 이후 활성화
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final AuthenticationConfiguration authenticationConfiguration;

	public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService,
		AuthenticationConfiguration authenticationConfiguration) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
		this.authenticationConfiguration = authenticationConfiguration;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
		filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
		return filter;
	}

	@Bean
	public JwtAuthorizationFilter jwtAuthorizationFilter() {
		return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// CSRF 설정
		http.csrf((csrf) -> csrf.disable());

		// 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
		http.sessionManagement((sessionManagement) ->
			sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);

		http.authorizeHttpRequests((authorizeHttpRequests) ->
				authorizeHttpRequests
					.requestMatchers("/error").permitAll()
					.requestMatchers("/h2/**").permitAll()
					.requestMatchers("/v1/customers").permitAll()
					.requestMatchers("/v1/email/send-verification").permitAll()
					.requestMatchers("/v1/email/verify").permitAll()
					//TODO: 추후 개발 완성전 manager등록 API 필터 거치도록 변경
					.requestMatchers("/v1/managers").permitAll()
					.requestMatchers("/v1/owners").permitAll()
					.requestMatchers("/v1/auth/login").permitAll()
					.requestMatchers("/v1/auth/logout").authenticated()
					.anyRequest().authenticated() // 그 외 모든 요청 인증처리
			)

			.headers(headers -> headers
				.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
			);

		http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
