package com.sparta.delivery.backend.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqCreateAiPromptDto {

	@NotBlank(message = "요청 메세지가 비어있습니다.")
	@Size(max = 200, message = "요청 메세지는 최대 200자까지 가능합니다.")
	private String reqMessage;

}
