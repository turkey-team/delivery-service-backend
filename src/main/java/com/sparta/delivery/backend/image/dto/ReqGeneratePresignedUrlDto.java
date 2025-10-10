package com.sparta.delivery.backend.image.dto;

import lombok.Getter;

@Getter
public class ReqGeneratePresignedUrlDto {
	private String fileName;
	private String contentType;
}
