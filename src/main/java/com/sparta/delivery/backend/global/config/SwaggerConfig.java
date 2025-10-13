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
	public GroupedOpenApi allApi() {
		return GroupedOpenApi.builder()
			.group("전체 API")
			.pathsToMatch("/v1/**")  // v1으로 시작하는 모든 경로
			.build();
	}
}