package com.sparta.delivery.backend.ai.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sparta.delivery.backend.ai.dto.ReqCreateAiPromptDto;
import com.sparta.delivery.backend.ai.dto.ResCreateAiPromptDto;
import com.sparta.delivery.backend.ai.dto.ResReadAiPromptDto;
import com.sparta.delivery.backend.ai.entity.AiPrompt;
import com.sparta.delivery.backend.ai.repository.AiPromptRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiPromptService {

	private final AiPromptRepository aiPromptRepository;
	private final GoogleAiService googleAiService;

	public ResCreateAiPromptDto createAiPrompt(ReqCreateAiPromptDto requestDto) {
		String resMessage = googleAiService.createAiPrompt(requestDto.getReqMessage());

		AiPrompt aiPrompt = AiPrompt.builder()
			.reqMessage(requestDto.getReqMessage())
			.resMessage(resMessage)
			.build();

		AiPrompt savedAiPrompt = aiPromptRepository.save(aiPrompt);

		return ResCreateAiPromptDto.from(savedAiPrompt);
	}

	public Page<ResReadAiPromptDto> getAllAiPrompts(Pageable pageable) {
		return aiPromptRepository.findAll(pageable)
			.map(ResReadAiPromptDto::from);
	}

}
