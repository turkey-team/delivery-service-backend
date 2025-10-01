package com.sparta.delivery.backend.ai.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sparta.delivery.backend.ai.dto.AiPromptCreateRequestDto;
import com.sparta.delivery.backend.ai.dto.AiPromptCreateResponseDto;
import com.sparta.delivery.backend.ai.dto.AiPromptReadResponseDto;
import com.sparta.delivery.backend.ai.entity.AiPrompt;
import com.sparta.delivery.backend.ai.repository.AiPromptRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiPromptService {

	private final AiPromptRepository aiPromptRepository;
	private final GoogleAiService googleAiService;

	public AiPromptCreateResponseDto createAiPrompt(AiPromptCreateRequestDto requestDto) {
		String resMessage = googleAiService.createAiPrompt(requestDto.getReqMessage());

		AiPrompt aiPrompt = AiPrompt.builder()
			.reqMessage(requestDto.getReqMessage())
			.resMessage(resMessage)
			.build();

		AiPrompt savedAiPrompt = aiPromptRepository.save(aiPrompt);

		return AiPromptCreateResponseDto.from(savedAiPrompt);
	}

	public Page<AiPromptReadResponseDto> getAllAiPrompts(Pageable pageable) {
		return aiPromptRepository.findAll(pageable)
			.map(AiPromptReadResponseDto::from);
	}

}
