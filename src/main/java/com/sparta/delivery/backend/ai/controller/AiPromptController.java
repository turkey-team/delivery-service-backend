package com.sparta.delivery.backend.ai.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.ai.dto.AiPromptCreateRequestDto;
import com.sparta.delivery.backend.ai.dto.AiPromptCreateResponseDto;
import com.sparta.delivery.backend.ai.dto.AiPromptReadResponseDto;
import com.sparta.delivery.backend.ai.service.AiPromptService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/ai-prompts")
@RequiredArgsConstructor
public class AiPromptController {

	private final AiPromptService aiPromptService;

	@PostMapping
	public ResponseEntity<AiPromptCreateResponseDto> createAiPrompt(@RequestBody AiPromptCreateRequestDto requestDto) {
		AiPromptCreateResponseDto responseDto = aiPromptService.createAiPrompt(requestDto);

		return ResponseEntity.ok(responseDto);
	}

	@GetMapping
	public ResponseEntity<Page<AiPromptReadResponseDto>> getAllAiPrompt(Pageable pageable) {
		Page<AiPromptReadResponseDto> responseDtoList = aiPromptService.getAllAiPrompts(pageable);

		return ResponseEntity.ok(responseDtoList);
	}

}
