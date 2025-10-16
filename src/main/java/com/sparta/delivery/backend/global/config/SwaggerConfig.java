package com.sparta.delivery.backend.global.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import com.sparta.delivery.backend.security.dto.LoginRequestDto;
import com.sparta.delivery.backend.security.filter.JwtAuthenticationFilter;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
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
		version = "v1.0.0"),
	security = { @SecurityRequirement(name = "Authentication") }
)
@SecurityScheme(
	name = "Authentication",
	type = SecuritySchemeType.HTTP,
	bearerFormat = "JWT",
	scheme = "bearer",
	in = SecuritySchemeIn.HEADER
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
			.addOpenApiCustomizer(jwtLoginEndpointCustomizer(applicationContext, jwtAuthenticationFilter))
			.addOperationCustomizer(operationCustomizer())
			.build();
	}

	@Bean
	public OperationCustomizer operationCustomizer() {
		return (operation, handlerMethod) -> {
			PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);

			if (preAuthorize == null) {
				preAuthorize = handlerMethod.getBeanType().getAnnotation(PreAuthorize.class);
			}

			StringBuilder authInfo = new StringBuilder("\n\n---\n");

			if (preAuthorize != null) {
				String expression = preAuthorize.value();
				List<String> roles = extractRoles(expression);

				authInfo.append("### 🔒 인증 요구사항\n");

				if (!roles.isEmpty()) {
					authInfo.append("- 👤 필요한 권한: ");
					authInfo.append(String.join(", ", roles));
					authInfo.append("\n");
				} else if (expression.contains("isAuthenticated()")) {
					authInfo.append("- ✅ 로그인 필수\n");
				}
			} else {
				// PreAuthorize가 없는 경우 - 인증 불필요
				authInfo.append("### 🌐 인증 요구사항\n");
				authInfo.append("- 🔓 로그인 불필요 (Public API)\n");
			}

			String currentDescription = operation.getDescription() != null
				? operation.getDescription()
				: "";

			operation.setDescription(currentDescription + authInfo);

			return operation;
		};
	}

	private List<String> extractRoles(String expression) {
		List<String> roles = new ArrayList<>();

		// hasRole('ROLE') 패턴 찾기
		Pattern pattern = Pattern.compile("hasRole\\('([^']+)'\\)");
		Matcher matcher = pattern.matcher(expression);

		while (matcher.find()) {
			roles.add(matcher.group(1));
		}

		// hasAnyRole('ROLE1', 'ROLE2') 패턴 찾기
		pattern = Pattern.compile("hasAnyRole\\(([^)]+)\\)");
		matcher = pattern.matcher(expression);

		if (matcher.find()) {
			String rolesStr = matcher.group(1);
			String[] roleArray = rolesStr.split(",");
			for (String role : roleArray) {
				roles.add(role.trim().replace("'", ""));
			}
		}

		return roles;
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