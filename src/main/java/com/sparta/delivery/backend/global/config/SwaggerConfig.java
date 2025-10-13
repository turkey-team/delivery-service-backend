package com.sparta.delivery.backend.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	info = @Info(title = "Delivery Backend Service API",
		description = "Delivery Backend Service의 REST API 명세서입니다.",
		version = "v1.0.0")
)
@Configuration
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi reviewApi() {
		String[] paths = {
			"/v1/stores/**/reviews",
			"/v1/stores/**/reviews/**",
			"/v1/customer/**/reviews",
			"/v1/stores/**/review"
		};

		return GroupedOpenApi.builder()
			.group("리뷰 API")
			.pathsToMatch(paths)
			.build();
	}

	@Bean
	public GroupedOpenApi replyApi() {
		return GroupedOpenApi.builder()
			.group("답글 API")
			.pathsToMatch("/v1/replies/**")
			.build();
	}

	@Bean
	public GroupedOpenApi storeMenuApi() {
		return GroupedOpenApi.builder()
				.group("가게 메뉴 API")
				.pathsToMatch("/v1/stores/**/menus/**")
				.build();
	}

	@Bean
	public GroupedOpenApi orderApi() {
		return GroupedOpenApi.builder()
				.group("주문 API")
				.pathsToMatch("/v1/orders/**")
				.build();
	}
}