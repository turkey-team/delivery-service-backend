package com.sparta.delivery.backend.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.ai.dto.AiPromptRequestDto;
import com.sparta.delivery.backend.ai.dto.AiPromptResponseDto;
import com.sparta.delivery.backend.ai.service.AiPromptService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/ai-prompts")
@RequiredArgsConstructor
public class AiPromptController {

	private final AiPromptService aiPromptService;

	@PostMapping
	public ResponseEntity<AiPromptResponseDto> createAiPrompt(@RequestBody AiPromptRequestDto requestDto) {
		AiPromptResponseDto responseDto = aiPromptService.createAiPrompt(requestDto);

		return ResponseEntity.ok(responseDto);
	}

}
