package com.sparta.delivery.backend.ai.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.ai.dto.ReqCreateAiPromptDto;
import com.sparta.delivery.backend.ai.dto.ResCreateAiPromptDto;
import com.sparta.delivery.backend.ai.dto.ResReadAiPromptDto;
import com.sparta.delivery.backend.ai.entity.AiPrompt;
import com.sparta.delivery.backend.ai.repository.AiPromptRepository;
import com.sparta.delivery.backend.global.common.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiPromptService {

	private final AiPromptRepository aiPromptRepository;
	private final GoogleAiService googleAiService;

	@Transactional
	public ResCreateAiPromptDto createAiPrompt(ReqCreateAiPromptDto requestDto) {
		String resMessage = googleAiService.createAiPrompt(requestDto.getReqMessage());

		AiPrompt aiPrompt = AiPrompt.builder()
			.reqMessage(requestDto.getReqMessage())
			.resMessage(resMessage)
			.build();

		AiPrompt savedAiPrompt = aiPromptRepository.save(aiPrompt);

		return ResCreateAiPromptDto.from(savedAiPrompt);
	}

	@Transactional(readOnly = true)
	public PageResponse<ResReadAiPromptDto> getAllAiPrompts(Pageable pageable) {
		Page<ResReadAiPromptDto> responseDtoPage = aiPromptRepository.findAll(pageable)
			.map(ResReadAiPromptDto::from);

		return PageResponse.of(responseDtoPage);
	}

}
