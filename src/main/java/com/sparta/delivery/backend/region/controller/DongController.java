package com.sparta.delivery.backend.region.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.region.dto.ReqCreateDongDto;
import com.sparta.delivery.backend.region.dto.ResCreateDongDto;
import com.sparta.delivery.backend.region.dto.ResReadDongDto;
import com.sparta.delivery.backend.region.dto.ResUpdateDongDto;
import com.sparta.delivery.backend.region.service.DongService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/regions")
@RequiredArgsConstructor
public class DongController {

	private final DongService dongService;

	@PostMapping("/sigungus/{sigunguId}/dongs")
	public ResponseEntity<List<ResCreateDongDto>> createDongs(
		@PathVariable UUID sigunguId, @RequestBody List<@Valid ReqCreateDongDto> requestDtoList
	) {
		List<ResCreateDongDto> responseDtoList = dongService.createDongs(sigunguId, requestDtoList);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDtoList);
	}

	@GetMapping("/sigungus/{sigunguId}/dongs")
	public ResponseEntity<List<ResReadDongDto>> getAllDong(@PathVariable UUID sigunguId) {
		List<ResReadDongDto> responseDtoList = dongService.getAllDong(sigunguId);

		return ResponseEntity.ok(responseDtoList);
	}

	@PutMapping("/sigungus/{sigunguId}/dongs/{dongId}")
	public ResponseEntity<ResUpdateDongDto> updateDong(
		@PathVariable UUID sigunguId, @PathVariable UUID dongId, @Valid @RequestBody ReqCreateDongDto requestDto
	) {
		ResUpdateDongDto responseDto = dongService.updateDong(sigunguId, dongId, requestDto);

		return ResponseEntity.ok(responseDto);
	}

	@DeleteMapping("/sigungus/{sigunguId}/dongs/{dongId}")
	public ResponseEntity<Void> deleteDong(@PathVariable UUID sigunguId, @PathVariable UUID dongId) {
		dongService.deleteDong(sigunguId, dongId);

		return ResponseEntity.noContent().build();
	}

}
