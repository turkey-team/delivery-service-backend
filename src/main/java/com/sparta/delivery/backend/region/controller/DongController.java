package com.sparta.delivery.backend.region.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.region.dto.ReqCreateDongDto;
import com.sparta.delivery.backend.region.dto.ResCreateDongDto;
import com.sparta.delivery.backend.region.dto.ResReadDongDto;
import com.sparta.delivery.backend.region.service.DongService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/regions")
@RequiredArgsConstructor
public class DongController {

	private final DongService dongService;

	@PostMapping("/sigungu/{sigunguId}/dong")
	public ResponseEntity<ResCreateDongDto> createDong(
		@PathVariable UUID sigunguId, @RequestBody ReqCreateDongDto requestDto
	) {
		ResCreateDongDto responseDto = dongService.createDong(sigunguId, requestDto);

		return ResponseEntity.ok(responseDto);
	}

	@GetMapping("/sigungu/{sigunguId}/dong")
	public ResponseEntity<List<ResReadDongDto>> getAllDong(@PathVariable UUID sigunguId) {
		List<ResReadDongDto> responseDtoList = dongService.getAllDong(sigunguId);

		return ResponseEntity.ok(responseDtoList);
	}

}
