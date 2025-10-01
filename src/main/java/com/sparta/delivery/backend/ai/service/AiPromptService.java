package com.sparta.delivery.backend.ai.service;

import org.springframework.stereotype.Service;

import com.sparta.delivery.backend.ai.dto.AiPromptRequestDto;
import com.sparta.delivery.backend.ai.dto.AiPromptResponseDto;
import com.sparta.delivery.backend.ai.entity.AiPrompt;
import com.sparta.delivery.backend.ai.repository.AiPromptRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiPromptService {

	private final AiPromptRepository aiPromptRepository;
	private final GoogleAiService googleAiService;

	public AiPromptResponseDto createAiPrompt(AiPromptRequestDto requestDto) {
		String resMessage = googleAiService.createAiPrompt(requestDto.getReqMessage());

		AiPrompt aiPrompt = AiPrompt.builder()
			.reqMessage(requestDto.getReqMessage())
			.resMessage(resMessage)
			.build();

		AiPrompt savedAiPrompt = aiPromptRepository.save(aiPrompt);

		return new AiPromptResponseDto(savedAiPrompt.getResMessage());
	}

}
