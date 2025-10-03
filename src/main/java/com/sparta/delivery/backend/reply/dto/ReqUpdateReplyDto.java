package com.sparta.delivery.backend.reply.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReqUpdateReplyDto {

	@NotBlank(message = "답글 내용은 비워둘 수 없습니다.")
	private String context;

}
