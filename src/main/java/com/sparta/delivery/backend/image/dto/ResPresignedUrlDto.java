package com.sparta.delivery.backend.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResPresignedUrlDto {

	@Schema(description = "Presigned URL", example = "https://bucket.s3..", requiredMode = Schema.RequiredMode.REQUIRED)
	private  String presignedUrl;

	@Schema(description = "파일 경로", example = "uploads/6cafc_img1.png", requiredMode = Schema.RequiredMode.REQUIRED)
	private String filePath;

	@Schema(description = "파일 이름", example = "img1.png", requiredMode = Schema.RequiredMode.REQUIRED)
	private String fileName;

	public static ResPresignedUrlDto of(String presignedUrl, String filePath, String fileName) {
		return ResPresignedUrlDto.builder()
			.presignedUrl(presignedUrl)
			.filePath(filePath)
			.fileName(fileName)
			.build();
	}
}
