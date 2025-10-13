package com.sparta.delivery.backend.image.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.address.dto.ResAddressDto;
import com.sparta.delivery.backend.image.dto.ReqGeneratePresignedUrlDto;
import com.sparta.delivery.backend.image.dto.ResPresignedUrlDto;
import com.sparta.delivery.backend.image.service.S3Service;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "S3 API V1", description = "S3 관련 API")
@RestController
@RequestMapping("/v1/s3")
@RequiredArgsConstructor
public class S3Controller {

	private final S3Service s3Service;

	/**
	 * 다중 파일 업로드용 Presigned URL 발급
	 * POST /v1/s3/presigned-upload
	 */
	@Operation(summary = "Presigned URL 발급",
		description = "다중 파일 업로드용 Presigned URL을 발급합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Presigned URL 발급 성공", content = @Content(
			array = @ArraySchema(schema = @Schema(implementation = ResPresignedUrlDto.class)))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
		@ApiResponse(responseCode = "403", description = "Authorization 헤더가 없거나 권한 없음", content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류 (S3 연결 실패 등)", content = @Content)
	})
	@PostMapping("/presigned-upload")
	public ResponseEntity<List<ResPresignedUrlDto>> generatePresignedUrls(
		@Valid @RequestBody List<ReqGeneratePresignedUrlDto> requests,
		@AuthenticationPrincipal UserDetailsImpl user
	) {
		return ResponseEntity.ok(s3Service.generatePresignedUrls(requests));
	}
}
