package com.sparta.delivery.backend.image.service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sparta.delivery.backend.image.dto.ReqGeneratePresignedUrlDto;
import com.sparta.delivery.backend.image.dto.ResPresignedUrlDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Presigner s3Presigner;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Value("${cloud.aws.s3.presigned-url-expiration-minutes}")
	private Duration expirationMinutes;

	/**
	 * 다중 파일 업로드용 Presigned URL 생성
	 */
	public List<ResPresignedUrlDto> generatePresignedUrls(List<ReqGeneratePresignedUrlDto> requests) {
		return requests.stream()
			.map(this::generatePresignedUrl)
			.toList();
	}

	private ResPresignedUrlDto generatePresignedUrl(ReqGeneratePresignedUrlDto request) {
		String filePath = buildFilePath(request.getFileName());
		PresignedPutObjectRequest presignedRequest = createPresignedPutRequest(request, filePath);
		return ResPresignedUrlDto.of(presignedRequest.url().toString(), filePath, request.getFileName());
	}

	private PresignedPutObjectRequest createPresignedPutRequest(ReqGeneratePresignedUrlDto request, String filePath) {
		PutObjectRequest putObjectRequest = buildPutObjectRequest(request, filePath);
		PutObjectPresignRequest presignRequest = buildPutObjectPresignRequest(putObjectRequest);
		return s3Presigner.presignPutObject(presignRequest);
	}

	private PutObjectRequest buildPutObjectRequest(ReqGeneratePresignedUrlDto request, String filePath) {
		return PutObjectRequest.builder()
			.bucket(bucketName)
			.key(filePath)
			.contentType(request.getContentType())
			.build();
	}

	private PutObjectPresignRequest buildPutObjectPresignRequest(PutObjectRequest putObjectRequest) {
		return PutObjectPresignRequest.builder()
			.signatureDuration(expirationMinutes)
			.putObjectRequest(putObjectRequest)
			.build();
	}

	private String buildFilePath(String fileName) {
		String uuid = UUID.randomUUID().toString();
		return String.format("uploads/%s_%s", uuid, fileName);
	}
}
