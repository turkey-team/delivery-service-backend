package com.sparta.delivery.backend.region.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.region.dto.ReqCreateSigunguDto;
import com.sparta.delivery.backend.region.dto.ResCreateSigunguDto;
import com.sparta.delivery.backend.region.service.SigunguService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/regions")
@RequiredArgsConstructor
public class SigunguController {

	private final SigunguService sigunguService;

	@PostMapping("/sido/{sidoId}/sigungu")
	public ResponseEntity<ResCreateSigunguDto> createSigungu(
		@PathVariable UUID sidoId, @RequestBody ReqCreateSigunguDto requestDto
	) {
		ResCreateSigunguDto responseDto = sigunguService.createSigungu(sidoId, requestDto);

		return ResponseEntity.ok(responseDto);
	}

}
