package com.sparta.delivery.backend.global.config;

import java.util.Optional;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import com.sparta.delivery.backend.security.dto.LoginRequestDto;
import com.sparta.delivery.backend.security.filter.JwtAuthenticationFilter;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

@OpenAPIDefinition(
	info = @Info(title = "Delivery Backend Service API",
		description = "Delivery Backend Service의 REST API 명세서입니다.",
		version = "v1.0.0")
)
@Configuration
public class SwaggerConfig {

	private final ApplicationContext applicationContext;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SwaggerConfig(ApplicationContext applicationContext, JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.applicationContext = applicationContext;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public GroupedOpenApi allApi() {
		return GroupedOpenApi.builder()
			.group("전체 API")
			.pathsToMatch("/v1/**")
			.addOpenApiCustomizer(jwtLoginEndpointCustomizer(applicationContext, jwtAuthenticationFilter)) // v1으로 시작하는 모든 경로
			.build();
	}

	@Bean
	public OpenApiCustomizer jwtLoginEndpointCustomizer(ApplicationContext applicationContext, JwtAuthenticationFilter jwtAuthenticationFilter) {
		FilterChainProxy filterChainProxy = applicationContext.getBean(
			AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME,
			FilterChainProxy.class
		);

		return openAPI -> {
			for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
				// JwtAuthenticationFilter 찾기
				Optional<JwtAuthenticationFilter> optionalFilter = filterChain.getFilters().stream()
					.filter(JwtAuthenticationFilter.class::isInstance)
					.map(JwtAuthenticationFilter.class::cast)
					.findAny();

				if (optionalFilter.isPresent()) {
					JwtAuthenticationFilter filter = optionalFilter.get();

					// Swagger에 추가할 Operation 생성
					Operation operation = new Operation()
						.summary("로그인 및 JWT 발급")
						.description("사용자 아이디와 비밀번호로 로그인 후 AccessToken과 RefreshToken을 발급받습니다.");

					// 요청 스키마 정의
					Schema<LoginRequestDto> loginSchema = new ObjectSchema()
						.addProperties("username", new StringSchema().example("customer1"))
						.addProperties("password", new StringSchema().example("Password1!"));

					RequestBody requestBody = new RequestBody()
						.required(true)
						.content(new Content().addMediaType(
							org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
							new io.swagger.v3.oas.models.media.MediaType().schema(loginSchema)
						));
					operation.requestBody(requestBody);

					// 응답 정의
					ApiResponses responses = new ApiResponses()
						.addApiResponse(String.valueOf(HttpStatus.OK.value()),
							new ApiResponse().description("로그인 성공 — 헤더에 AccessToken, 쿠키에 RefreshToken이 포함됩니다."))
						.addApiResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()),
							new ApiResponse().description("인증 실패 — 잘못된 아이디 또는 비밀번호"));

					operation.responses(responses);
					operation.addTagsItem("auth-controller");

					// PathItem 추가
					PathItem pathItem = new PathItem().post(operation);
					openAPI.path("/v1/auth/login", pathItem);
				}
			}
		};
	}
}