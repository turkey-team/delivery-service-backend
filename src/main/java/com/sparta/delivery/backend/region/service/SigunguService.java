package com.sparta.delivery.backend.region.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.region.dto.ReqCreateSigunguDto;
import com.sparta.delivery.backend.region.dto.ReqUpdateSigunguDto;
import com.sparta.delivery.backend.region.dto.ResCreateSigunguDto;
import com.sparta.delivery.backend.region.dto.ResReadSigunguDto;
import com.sparta.delivery.backend.region.dto.ResUpdateSigunguDto;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.region.exception.RegionAlreadyExistsException;
import com.sparta.delivery.backend.region.exception.RegionDuplicateRequestException;
import com.sparta.delivery.backend.region.exception.RegionNotFoundException;
import com.sparta.delivery.backend.region.repository.SidoRepository;
import com.sparta.delivery.backend.region.repository.SigunguRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SigunguService {

	private final SigunguRepository sigunguRepository;
	private final SidoRepository sidoRepository;

	// 시·군·구 생성
	@Transactional
	public List<ResCreateSigunguDto> createSigungus(UUID sidoId, List<ReqCreateSigunguDto> requestDtoList) {
		Sido sido = sidoRepository.findByIdAndDeletedAtIsNull(sidoId).orElseThrow(() -> {
			log.warn("시/도 지역 검색 실패");
			return new RegionNotFoundException("존재하지 않는 시/도입니다.");
		});

		List<String> names = requestDtoList.stream()
			.map(ReqCreateSigunguDto::getName)
			.distinct()
			.toList();

		if (names.size() != requestDtoList.size()) {
			log.warn("시/군/구 지역 이름 중복 : Request");
			throw new RegionDuplicateRequestException("요청에 중복된 시/도 이름이 포함되어 있습니다.");
		}

		if (sigunguRepository.existsByNameInAndSidoAndDeletedAtIsNull(names, sido)) {
			log.warn("시/군/구 지역 이름 중복");
			throw new RegionAlreadyExistsException("이미 존재하는 시/군/구 이름이 포함되어 있습니다.");
		}

		List<String> codes = requestDtoList.stream()
			.map(ReqCreateSigunguDto::getCode)
			.distinct()
			.toList();

		if (codes.size() != requestDtoList.size()) {
			log.warn("시/군/구 지역 코드 중복 : Request");
			throw new RegionDuplicateRequestException("요청에 중복된 시/도 코드가 포함되어 있습니다.");
		}

		if (sigunguRepository.existsByCodeInAndDeletedAtIsNull(codes)) {
			log.warn("시/군/구 지역 코드 중복");
			throw new RegionAlreadyExistsException("이미 존재하는 시/군/구 코드가 포함되어 있습니다.");
		}

		List<Sigungu> sigunguList = requestDtoList.stream()
			.map(requestDto -> Sigungu.builder()
				.sido(sido)
				.name(requestDto.getName())
				.code(requestDto.getCode())
				.build()
			)
			.toList();

		return sigunguRepository.saveAll(sigunguList).stream()
			.map(ResCreateSigunguDto::from)
			.toList();
	}

	// 시·군·구 목록 조회
	@Transactional(readOnly = true)
	public List<ResReadSigunguDto> getAllSigungu(UUID sidoId) {
		Sido sido = sidoRepository.findByIdAndDeletedAtIsNull(sidoId).orElseThrow(() -> {
			log.warn("시/도 지역 검색 실패");
			return new RegionNotFoundException("존재하지 않는 시/도입니다.");
		});

		return sigunguRepository.findAllBySidoAndDeletedAtIsNull(sido).stream()
			.map(ResReadSigunguDto::from)
			.toList();
	}

	// 시·군·구 수정
	@Transactional
	public ResUpdateSigunguDto updateSigungu(UUID sidoId, UUID sigunguId, ReqUpdateSigunguDto requestDto) {
		Sido sido = sidoRepository.findByIdAndDeletedAtIsNull(sidoId).orElseThrow(() -> {
			log.warn("시/도 지역 검색 실패");
			return new RegionNotFoundException("존재하지 않는 시/도입니다.");
		});

		Sigungu sigungu = sigunguRepository.findByIdAndSidoAndDeletedAtIsNull(sigunguId, sido).orElseThrow(() -> {
			log.warn("시/군/구 지역 검색 실패");
			return new RegionNotFoundException("존재하지 않는 시/군/구입니다.");
		});

		if (sigunguRepository.existsByNameAndSidoAndIdNotAndDeletedAtIsNull(requestDto.getName(), sido,
			sigungu.getId())) {
			log.warn("시/군/구 지역 이름 중복");
			throw new RegionAlreadyExistsException("이미 존재하는 시/군/구 이름입니다.");
		}

		if (sigunguRepository.existsByCodeAndIdNotAndDeletedAtIsNull(requestDto.getCode(), sigungu.getId())) {
			log.warn("시/군/구 지역 코드 중복");
			throw new RegionAlreadyExistsException("이미 존재하는 시/군/구 코드입니다.");
		}

		sigungu.update(sido, requestDto.getName(), requestDto.getCode());

		return ResUpdateSigunguDto.from(sigungu);
	}

	// 시·군·구 삭제
	@Transactional
	public void deleteSigungu(UUID sidoId, UUID sigunguId, Long loginUserId) {
		Sido sido = sidoRepository.findByIdAndDeletedAtIsNull(sidoId).orElseThrow(() -> {
			log.warn("시/도 지역 검색 실패");
			return new RegionNotFoundException("존재하지 않는 시/도입니다.");
		});

		Sigungu sigungu = sigunguRepository.findByIdAndSidoAndDeletedAtIsNull(sigunguId, sido).orElseThrow(() -> {
			log.warn("시/군/구 지역 검색 실패");
			return new RegionNotFoundException("존재하지 않는 시/군/구입니다.");
		});

		sigungu.softDelete(loginUserId);
	}

}
