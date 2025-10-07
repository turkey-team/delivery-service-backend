package com.sparta.delivery.backend.ai.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.ai.entity.AiPrompt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResReadAiPromptDto {

	private UUID id;

	private String reqMessage;

	private String resMessage;

	private Instant createdAt;

	private Long createdBy;

	public static ResReadAiPromptDto from(AiPrompt aiPrompt) {
		return new ResReadAiPromptDto(
			aiPrompt.getId(),
			aiPrompt.getReqMessage(),
			aiPrompt.getResMessage(),
			aiPrompt.getCreatedAt(),
			aiPrompt.getCreatedBy()
		);
	}

}
