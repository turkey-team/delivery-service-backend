package com.sparta.delivery.backend.image.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReqGeneratePresignedUrlDto {

	@NotBlank(message = "파일 이름은 필수 입력 값입니다.")
	private String fileName;

	@NotBlank(message = "컨텐츠 타입은 필수 입력 값입니다.")
	private String contentType;

}
