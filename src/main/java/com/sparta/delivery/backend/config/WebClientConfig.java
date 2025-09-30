package com.sparta.delivery.backend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Value("${googleai.api.baseurl}")
	private String googleAiBaseUrl;

	@Bean
	@Qualifier("googleAi")
	public WebClient googleAiWebClient() {
		return WebClient.builder()
			.baseUrl(googleAiBaseUrl)
			.build();
	}

}
