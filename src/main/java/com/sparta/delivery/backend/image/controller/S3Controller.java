package com.sparta.delivery.backend.image.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.image.dto.ReqGeneratePresignedUrlDto;
import com.sparta.delivery.backend.image.dto.ResPresignedUrlDto;
import com.sparta.delivery.backend.image.service.S3Service;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/s3")
@RequiredArgsConstructor
public class S3Controller {

	private final S3Service s3Service;

	/**
	 * 다중 파일 업로드용 Presigned URL 발급
	 * POST /v1/s3/presigned-upload
	 */
	@PostMapping("/presigned-upload")
	public ResponseEntity<List<ResPresignedUrlDto>> generatePresignedUrls(
		@RequestBody List<ReqGeneratePresignedUrlDto> requests,
		@AuthenticationPrincipal UserDetailsImpl user
	) {
		return ResponseEntity.ok(s3Service.generatePresignedUrls(requests));
	}
}
