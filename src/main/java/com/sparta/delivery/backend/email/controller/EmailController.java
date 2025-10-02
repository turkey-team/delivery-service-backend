package com.sparta.delivery.backend.email.controller;

import com.sparta.delivery.backend.email.dto.ReqEmailVerificationCheckDto;
import com.sparta.delivery.backend.email.dto.ResEmailVerificationCheckDto;
import com.sparta.delivery.backend.email.dto.ReqEmailVerificationDto;
import com.sparta.delivery.backend.email.dto.ResEmailVerificationDto;
import com.sparta.delivery.backend.email.service.EmailVerificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/email")
@RequiredArgsConstructor
public class EmailController {

	private final EmailVerificationService emailVerificationService;

	/**
	 * 이메일 인증 코드 전송
	 * POST /api/email/send-verification
	 */
	@PostMapping("/send-verification")
	public ResponseEntity<ResEmailVerificationDto> sendVerificationCode(
		@Valid @RequestBody ReqEmailVerificationDto requestDto) {

		ResEmailVerificationDto response = emailVerificationService.sendVerificationCode(requestDto);

		return ResponseEntity.ok(response);
	}

	/**
	 * 이메일 인증 코드 확인
	 * POST /api/email/verify
	 */
	@PostMapping("/verify")
	public ResponseEntity<ResEmailVerificationCheckDto> verifyCode(
		@Valid @RequestBody ReqEmailVerificationCheckDto requestDto) {

		ResEmailVerificationCheckDto response = emailVerificationService.verifyCode(requestDto);

		return ResponseEntity.ok(response);
	}
}
