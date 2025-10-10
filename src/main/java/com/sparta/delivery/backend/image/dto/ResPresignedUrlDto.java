package com.sparta.delivery.backend.image.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResPresignedUrlDto {
	private  String presignedUrl;
	private String filePath;
	private String fileName;

	public static ResPresignedUrlDto of(String presignedUrl, String filePath, String fileName) {
		return ResPresignedUrlDto.builder()
			.presignedUrl(presignedUrl)
			.filePath(filePath)
			.fileName(fileName)
			.build();
	}
}
