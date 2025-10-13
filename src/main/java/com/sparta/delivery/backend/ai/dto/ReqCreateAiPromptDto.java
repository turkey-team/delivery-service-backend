package com.sparta.delivery.backend.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqCreateAiPromptDto {

	@Schema(description = "요청 메세지", example = "치즈와 베이컨이 들어간 햄버거 메뉴의 음식 설명을 추천해줘")
	@NotBlank(message = "요청 메세지가 비어있습니다.")
	@Size(max = 200, message = "요청 메세지는 최대 200자까지 가능합니다.")
	private String reqMessage;

}
