package com.sparta.delivery.backend.reply.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ReqCreateReplyDto", description = "리뷰 답글 작성 요청 DTO")
public class ReqCreateReplyDto {

	@NotBlank(message = "답글 내용은 비워둘 수 없습니다.")
	@Schema(description = "작성할 답글 내용", example = "고객님 소중한 의견 감사합니다.", required = true)
	private String context;

}
