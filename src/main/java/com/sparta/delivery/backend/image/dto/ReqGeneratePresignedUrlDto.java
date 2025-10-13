package com.sparta.delivery.backend.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReqGeneratePresignedUrlDto {

	@Schema(description = "파일 이름", example = "image.png", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "파일 이름은 필수 입력 값입니다.")
	private String fileName;

	@Schema(description = "컨텐츠 타입", example = "image/png", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "컨텐츠 타입은 필수 입력 값입니다.")
	private String contentType;

}
