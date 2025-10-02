package com.sparta.delivery.backend.region.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.region.dto.ReqCreateSidoDto;
import com.sparta.delivery.backend.region.dto.ReqUpdateSidoDto;
import com.sparta.delivery.backend.region.dto.ResCreateSidoDto;
import com.sparta.delivery.backend.region.dto.ResReadSidoDto;
import com.sparta.delivery.backend.region.dto.ResUpdateSidoDto;
import com.sparta.delivery.backend.region.service.SidoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/regions")
@RequiredArgsConstructor
public class SidoController {

	private final SidoService sidoService;

	@PostMapping("/sidos")
	public ResponseEntity<ResCreateSidoDto> createSido(@RequestBody ReqCreateSidoDto requestDto) {
		ResCreateSidoDto responseDto = sidoService.createSido(requestDto);

		return ResponseEntity.ok(responseDto);
	}

	@GetMapping("/sidos")
	public ResponseEntity<List<ResReadSidoDto>> getAllSido() {
		List<ResReadSidoDto> responseDtoList = sidoService.getAllSido();

		return ResponseEntity.ok(responseDtoList);
	}

	@PutMapping("/sidos/{sidoId}")
	public ResponseEntity<ResUpdateSidoDto> updateSido(
		@PathVariable UUID sidoId, @RequestBody ReqUpdateSidoDto requestDto
	) {
		ResUpdateSidoDto responseDto = sidoService.updateSido(sidoId, requestDto);

		return ResponseEntity.ok(responseDto);
	}

	@DeleteMapping("/sidos/{sidoId}")
	public ResponseEntity<Void> deleteSido(@PathVariable UUID sidoId) {
		sidoService.deleteSido(sidoId);

		return ResponseEntity.noContent().build();
	}

}
