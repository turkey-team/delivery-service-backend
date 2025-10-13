package com.sparta.delivery.backend.ai.dto;

import com.sparta.delivery.backend.ai.entity.AiPrompt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResCreateAiPromptDto {

	@Schema(description = "생성된 AI 프롬프트", example = "고소한 치즈, 짭짤한 베이컨, 육즙 가득 패티의 환상적인 햄버거!")
	private String resMessage;

	public static ResCreateAiPromptDto from(AiPrompt aiPrompt) {
		return new ResCreateAiPromptDto(
			aiPrompt.getResMessage()
		);
	}

}
