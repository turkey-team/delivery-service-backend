package com.sparta.delivery.backend.region.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.region.dto.ReqCreateSigunguDto;
import com.sparta.delivery.backend.region.dto.ReqUpdateSigunguDto;
import com.sparta.delivery.backend.region.dto.ResCreateSigunguDto;
import com.sparta.delivery.backend.region.dto.ResReadSigunguDto;
import com.sparta.delivery.backend.region.dto.ResUpdateSigunguDto;
import com.sparta.delivery.backend.region.service.SigunguService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/regions")
@RequiredArgsConstructor
public class SigunguController {

	private final SigunguService sigunguService;

	@PostMapping("/sidos/{sidoId}/sigungus")
	@PreAuthorize("isAuthenticated() && hasRole('MANAGER')")
	public ResponseEntity<List<ResCreateSigunguDto>> createSigungus(
		@PathVariable UUID sidoId, @RequestBody List<@Valid ReqCreateSigunguDto> requestDtoList
	) {
		List<ResCreateSigunguDto> responseDtoList = sigunguService.createSigungus(sidoId, requestDtoList);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDtoList);
	}

	@GetMapping("/sidos/{sidoId}/sigungus")
	@PreAuthorize("isAuthenticated() && hasAnyRole('MANAGER', 'OWNER', 'CUSTOMER')")
	public ResponseEntity<List<ResReadSigunguDto>> getAllSigungu(@PathVariable UUID sidoId) {
		List<ResReadSigunguDto> responseDtoList = sigunguService.getAllSigungu(sidoId);

		return ResponseEntity.ok(responseDtoList);
	}

	@PutMapping("/sidos/{sidoId}/sigungus/{sigunguId}")
	@PreAuthorize("isAuthenticated() && hasRole('MANAGER')")
	public ResponseEntity<ResUpdateSigunguDto> updateSigungu(
		@PathVariable UUID sidoId, @PathVariable UUID sigunguId, @Valid @RequestBody ReqUpdateSigunguDto requestDto
	) {
		ResUpdateSigunguDto responseDto = sigunguService.updateSigungu(sidoId, sigunguId, requestDto);

		return ResponseEntity.ok(responseDto);
	}

	@DeleteMapping("/sidos/{sidoId}/sigungus/{sigunguId}")
	@PreAuthorize("isAuthenticated() && hasRole('MANAGER')")
	public ResponseEntity<Void> deleteSigungu(
		@PathVariable UUID sidoId, @PathVariable UUID sigunguId, @AuthenticationPrincipal UserDetailsImpl loginUser
	) {
		sigunguService.deleteSigungu(sidoId, sigunguId, loginUser.getId());

		return ResponseEntity.noContent().build();
	}

}
