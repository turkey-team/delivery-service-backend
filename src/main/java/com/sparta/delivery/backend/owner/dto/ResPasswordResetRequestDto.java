package com.sparta.delivery.backend.owner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(name = "ResPasswordResetRequestDto", description = "비밀번호 재설정 요청 응답")
public class ResPasswordResetRequestDto {
	
	@Schema(description = "응답 메시지", example = "해당 이메일로 가입된 계정이 있다면 비밀번호 재설정 링크가 발송됩니다.")
	private String message;
}
