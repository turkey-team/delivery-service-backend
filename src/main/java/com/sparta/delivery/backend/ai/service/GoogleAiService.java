package com.sparta.delivery.backend.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GoogleAiService {

	private final String model;
	private final float temperature;
	private final int maxLength;
	private final Client client;

	public GoogleAiService(
		@Value("${googleAi.api.key}") String key,
		@Value("${googleAi.api.model}") String model,
		@Value("${googleAi.api.temperature}") float temperature,
		@Value("${googleAi.api.maxLength}") int maxLength
	) {
		this.model = model;
		this.temperature = temperature;
		this.maxLength = maxLength;
		this.client = Client.builder()
			.apiKey(key)
			.build();
	}

	public String createAiPrompt(String reqMessage) {
		try {
			log.info("AI 프롬프트 생성");
			String text = String.format("%s (%d글자 이내로)", reqMessage, maxLength);
			GenerateContentConfig config = GenerateContentConfig.builder()
				.temperature(temperature)
				.maxOutputTokens(maxLength * 2)
				.build();
			GenerateContentResponse response = client.models.generateContent(model, text, config);

			return response.text();
		} catch (Exception e) {
			log.error("AI 프롬프트 생성 실패 {}", e.getMessage());
			throw new RuntimeException("AI 프롬프트 생성 중 오류가 발생했습니다.");
		}

	}

}
