package com.sparta.delivery.backend.ai.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.ai.dto.ReqCreateAiPromptDto;
import com.sparta.delivery.backend.ai.dto.ResCreateAiPromptDto;
import com.sparta.delivery.backend.ai.dto.ResReadAiPromptDto;
import com.sparta.delivery.backend.ai.service.AiPromptService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/ai-prompts")
@RequiredArgsConstructor
public class AiPromptController {

	private final AiPromptService aiPromptService;

	@PostMapping
	public ResponseEntity<ResCreateAiPromptDto> createAiPrompt(@Valid @RequestBody ReqCreateAiPromptDto requestDto) {
		ResCreateAiPromptDto responseDto = aiPromptService.createAiPrompt(requestDto);

		return ResponseEntity.ok(responseDto);
	}

	@GetMapping
	public ResponseEntity<Page<ResReadAiPromptDto>> getAllAiPrompt(Pageable pageable) {
		Page<ResReadAiPromptDto> responseDtoList = aiPromptService.getAllAiPrompts(pageable);

		return ResponseEntity.ok(responseDtoList);
	}

}
