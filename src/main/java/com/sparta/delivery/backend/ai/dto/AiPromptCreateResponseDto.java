package com.sparta.delivery.backend.ai.dto;

import com.sparta.delivery.backend.ai.entity.AiPrompt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiPromptCreateResponseDto {

	private String resMessage;

	public static AiPromptCreateResponseDto from(AiPrompt aiPrompt) {
		return new AiPromptCreateResponseDto(
			aiPrompt.getResMessage()
		);
	}

}
