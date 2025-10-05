package com.sparta.delivery.backend.reply.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(name = "ReqUpdateReplyDto", description = "리뷰 답글 수정 요청 DTO")
public class ReqUpdateReplyDto {

	@NotBlank(message = "답글 내용은 비워둘 수 없습니다.")
	@Schema(description = "수정할 답글 내용", example = "고객님 의견 반영하여 답글 수정 완료", required = true)
	private String context;

}
