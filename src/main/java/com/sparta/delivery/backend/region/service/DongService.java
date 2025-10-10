package com.sparta.delivery.backend.region.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.region.dto.ReqCreateDongDto;
import com.sparta.delivery.backend.region.dto.ReqUpdateDongDto;
import com.sparta.delivery.backend.region.dto.ResCreateDongDto;
import com.sparta.delivery.backend.region.dto.ResReadDongDto;
import com.sparta.delivery.backend.region.dto.ResUpdateDongDto;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.region.exception.RegionAlreadyExistsException;
import com.sparta.delivery.backend.region.exception.RegionDuplicateRequestException;
import com.sparta.delivery.backend.region.exception.RegionNotFoundException;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.region.repository.SigunguRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DongService {

	private final DongRepository dongRepository;
	private final SigunguRepository sigunguRepository;

	// 동 생성
	@Transactional
	public List<ResCreateDongDto> createDongs(UUID sigunguId, List<ReqCreateDongDto> requestDtoList) {
		Sigungu sigungu = sigunguRepository.findByIdCustom(sigunguId).orElseThrow(() -> {
			log.warn("시/군/구 지역 검색 실패");
			return new RegionNotFoundException("존재하지 않는 시/군/구입니다.");
		});

		List<String> names = requestDtoList.stream()
			.map(ReqCreateDongDto::getName)
			.distinct()
			.toList();

		if (names.size() != requestDtoList.size()) {
			log.warn("동 지역 이름 중복 : Request");
			throw new RegionDuplicateRequestException("요청에 중복된 동 이름이 포함되어 있습니다.");
		}

		if (dongRepository.existsByNameInAndSigunguCustom(names, sigungu)) {
			log.warn("동 지역 이름 중복");
			throw new RegionAlreadyExistsException("이미 존재하는 동 이름이 포함되어 있습니다.");
		}

		List<String> codes = requestDtoList.stream()
			.map(ReqCreateDongDto::getCode)
			.distinct()
			.toList();

		if (codes.size() != requestDtoList.size()) {
			log.warn("동 지역 코드 중복 : Request");
			throw new RegionDuplicateRequestException("요청에 중복된 동 코드가 포함되어 있습니다.");
		}

		if (dongRepository.existsByCodeInCustom(codes)) {
			log.warn("동 지역 코드 중복");
			throw new RegionAlreadyExistsException("이미 존재하는 동 코드가 포함되어 있습니다.");
		}

		List<Dong> dongList = requestDtoList.stream()
			.map(requestDto -> Dong.builder()
				.sigungu(sigungu)
				.name(requestDto.getName())
				.code(requestDto.getCode())
				.build()
			)
			.toList();

		return dongRepository.saveAll(dongList).stream()
			.map(ResCreateDongDto::from)
			.toList();
	}

	// 동 목록 조회
	public List<ResReadDongDto> getAllDong(UUID sigunguId) {
		Sigungu sigungu = sigunguRepository.findByIdCustom(sigunguId).orElseThrow(() -> {
			log.warn("시/군/구 지역 검색 실패");
			return new RegionNotFoundException("존재하지 않는 시/군/구입니다.");
		});

		return dongRepository.findAllBySigunguCustom(sigungu).stream()
			.map(ResReadDongDto::from)
			.toList();
	}

	// 동 수정
	@Transactional
	public ResUpdateDongDto updateDong(UUID sigunguId, UUID dongId, ReqUpdateDongDto requestDto) {
		Sigungu sigungu = sigunguRepository.findByIdCustom(sigunguId).orElseThrow(() -> {
			log.warn("시/군/구 지역 검색 실패");
			return new RegionNotFoundException("존재하지 않는 시/군/구입니다.");
		});

		Dong dong = dongRepository.findByIdAndSigunguCustom(dongId, sigungu).orElseThrow(() -> {
			log.warn("동 지역 검색 실패");
			return new RegionNotFoundException("존재하지 않는 동입니다.");
		});

		if (dongRepository.existsByNameAndSigunguAndIdNotCustom(requestDto.getName(), sigungu, dong.getId())) {
			log.warn("동 지역 이름 중복");
			throw new RegionAlreadyExistsException("이미 존재하는 동 이름입니다.");
		}

		if (dongRepository.existsByCodeAndIdNotCustom(requestDto.getCode(), dong.getId())) {
			log.warn("동 지역 코드 중복");
			throw new RegionAlreadyExistsException("이미 존재하는 동 코드입니다.");
		}

		dong.update(sigungu, requestDto.getName(), requestDto.getCode());

		return ResUpdateDongDto.from(dong);
	}

	// 동 삭제
	@Transactional
	public void deleteDong(UUID sigunguId, UUID dongId, Long loginUserId) {
		Sigungu sigungu = sigunguRepository.findByIdCustom(sigunguId).orElseThrow(() -> {
			log.warn("시/군/구 지역 검색 실패");
			return new RegionNotFoundException("존재하지 않는 시/군/구입니다.");
		});

		Dong dong = dongRepository.findByIdAndSigunguCustom(dongId, sigungu).orElseThrow(() -> {
			log.warn("동 지역 검색 실패");
			return new RegionNotFoundException("존재하지 않는 동입니다.");
		});

		dong.softDelete(loginUserId);
	}

}
