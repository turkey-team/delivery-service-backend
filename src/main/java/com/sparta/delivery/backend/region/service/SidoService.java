package com.sparta.delivery.backend.region.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.region.dto.ReqCreateSidoDto;
import com.sparta.delivery.backend.region.dto.ReqUpdateSidoDto;
import com.sparta.delivery.backend.region.dto.ResCreateSidoDto;
import com.sparta.delivery.backend.region.dto.ResReadSidoDto;
import com.sparta.delivery.backend.region.dto.ResUpdateSidoDto;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.repository.SidoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SidoService {

	private final SidoRepository sidoRepository;

	// 시·도 생성
	@Transactional
	public List<ResCreateSidoDto> createSidos(List<ReqCreateSidoDto> requestDtoList) {
		List<String> names = requestDtoList.stream()
			.map(ReqCreateSidoDto::getName)
			.toList();

		if (sidoRepository.existsByNameIn(names)) {
			log.warn("시/도 지역 이름 중복");
			throw new IllegalArgumentException("이미 존재하는 시/도 이름이 포함되어 있습니다.");
		}

		List<String> codes = requestDtoList.stream()
			.map(ReqCreateSidoDto::getCode)
			.toList();

		if (sidoRepository.existsByCodeIn(codes)) {
			log.warn("시/도 지역 코드 중복");
			throw new IllegalArgumentException("이미 존재하는 시/도 코드가 포함되어 있습니다.");
		}

		List<Sido> sidoList = requestDtoList.stream()
			.map(requestDto -> Sido.builder()
				.name(requestDto.getName())
				.code(requestDto.getCode())
				.build()
			)
			.toList();

		List<Sido> savedSidoList = sidoRepository.saveAll(sidoList);

		return savedSidoList.stream()
			.map(ResCreateSidoDto::from)
			.toList();
	}

	// 시·도 목록 조회
	@Transactional(readOnly = true)
	public List<ResReadSidoDto> getAllSido() {
		return sidoRepository.findAll().stream()
			.map(ResReadSidoDto::from)
			.toList();
	}

	// 시·도 수정
	@Transactional
	public ResUpdateSidoDto updateSido(UUID sidoId, ReqUpdateSidoDto requestDto) {
		Sido sido = sidoRepository.findById(sidoId).orElseThrow(() -> {
			log.warn("시/도 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/도입니다.");
		});

		if (sidoRepository.existsByNameAndIdNot(requestDto.getName(), sidoId)) {
			log.warn("시/도 지역 이름 중복");
			throw new IllegalArgumentException("이미 존재하는 시/도 이름입니다.");
		}

		if (sidoRepository.existsByCodeAndIdNot(requestDto.getCode(), sidoId)) {
			log.warn("시/도 지역 코드 중복");
			throw new IllegalArgumentException("이미 존재하는 시/도 코드입니다.");
		}

		sido.update(requestDto.getName(), requestDto.getCode());

		return ResUpdateSidoDto.from(sido);
	}

	// 시·도 삭제
	@Transactional
	public void deleteSido(UUID sidoId) {
		Sido sido = sidoRepository.findById(sidoId).orElseThrow(() -> {
			log.warn("시/도 지역 검색 실패");
			return new EntityNotFoundException("존재하지 않는 시/도입니다.");
		});

		// 임시로 null을 넘김
		sido.softDelete(null);
	}

}
