package com.sparta.delivery.backend.ai.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class GoogleAiService {

	private final WebClient webClient;
	private final String key;

	public GoogleAiService(@Qualifier("googleAi") WebClient webClient, @Value("${googleai.api.key}") String key) {
		this.webClient = webClient;
		this.key = key;
	}

	public String createAiPrompt(String reqMessage) {
		Map<String, Object> body = buildRequestBody(reqMessage);

		JsonNode jsonNode = webClient
			.post()
			.uri(uriBuilder -> uriBuilder
				.path("/v1beta/models/gemini-2.0-flash:generateContent")
				.queryParam("key", key)
				.build()
			)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(body)
			.retrieve()
			.bodyToMono(JsonNode.class)
			.block();

		return getAiPromptMessage(jsonNode);
	}

	// 요청 바디 생성
	private Map<String, Object> buildRequestBody(String reqMessage) {
		return Map.of("contents",
			List.of(Map.of("parts",
				List.of(Map.of("text", reqMessage))
			))
		);
	}

	// 응답 메세지 추출
	private String getAiPromptMessage(JsonNode jsonNode) {
		return Objects.requireNonNull(jsonNode)
			.path("candidates").get(0)
			.path("content")
			.path("parts").get(0)
			.path("text")
			.asText();
	}

}
