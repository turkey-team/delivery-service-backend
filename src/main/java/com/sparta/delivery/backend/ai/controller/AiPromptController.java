package com.sparta.delivery.backend.ai.controller;

import static com.sparta.delivery.backend.ai.internal.AiSwaggerMessage.*;

import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.ai.dto.ReqCreateAiPromptDto;
import com.sparta.delivery.backend.ai.dto.ResCreateAiPromptDto;
import com.sparta.delivery.backend.ai.dto.ResReadAiPromptDto;
import com.sparta.delivery.backend.ai.service.AiPromptService;
import com.sparta.delivery.backend.global.common.dto.PageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/ai-prompts")
@RequiredArgsConstructor
@Tag(name = "Ai-Prompt-Controller", description = "AI 프롬프트 관련 API")
public class AiPromptController {

	private final AiPromptService aiPromptService;

	@Operation(summary = "AI 프롬프트 생성",
		description = "요청한 메세지에 따라 AI 프롬프트를 작성합니다. MASTER, MANAGER, OWNER만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "AI 프롬프트가 생성되었습니다.",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResCreateAiPromptDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "잘못된 요청 형식", value = AI_INVALID_JSON),
				@ExampleObject(name = "요청 메세지 미입력", value = AI_REQUEST_EMPTY),
				@ExampleObject(name = "요청 메세지 최대 길이 초과", value = AI_REQUEST_TOO_LONG)
			})),
		@ApiResponse(responseCode = "403", description = "권한이 없습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "권한 부족", value = AI_FORBIDDEN),
			}))
	})
	@PostMapping
	@PreAuthorize("isAuthenticated() && hasAnyRole('MASTER', 'MANAGER', 'OWNER')")
	public ResponseEntity<ResCreateAiPromptDto> createAiPrompt(@Valid @RequestBody ReqCreateAiPromptDto requestDto) {
		ResCreateAiPromptDto responseDto = aiPromptService.createAiPrompt(requestDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@Operation(summary = "AI 프롬프트 목록 조회",
		description = "AI 프롬프트 요청 목록을 확인합니다. MASTER와 MANAGER만 사용 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "AI 프롬프트 요청 목록을 조회했습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(value = AI_PROMPT_LIST_EXAMPLE)
			})),
		@ApiResponse(responseCode = "403", description = "권한이 없습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "권한 부족", value = AI_FORBIDDEN),
			}))
	})
	@PageableAsQueryParam
	@GetMapping
	@PreAuthorize("isAuthenticated() && hasAnyRole('MASTER', 'MANAGER')")
	public ResponseEntity<PageResponse<ResReadAiPromptDto>> getAllAiPrompt(
		@ParameterObject @PageableDefault Pageable pageable) {
		PageResponse<ResReadAiPromptDto> responseDtoList = aiPromptService.getAllAiPrompts(pageable);

		return ResponseEntity.ok(responseDtoList);
	}

}
