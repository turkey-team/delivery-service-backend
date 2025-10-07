package com.sparta.delivery.backend.ai.dto;

import com.sparta.delivery.backend.ai.entity.AiPrompt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResCreateAiPromptDto {

	private String resMessage;

	public static ResCreateAiPromptDto from(AiPrompt aiPrompt) {
		return new ResCreateAiPromptDto(
			aiPrompt.getResMessage()
		);
	}

}
