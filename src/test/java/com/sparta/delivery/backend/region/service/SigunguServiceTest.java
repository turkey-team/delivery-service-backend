package com.sparta.delivery.backend.region.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
public class SigunguServiceTest {

	@Mock
	private SigunguRepository sigunguRepository;

	@Mock
	private SidoRepository sidoRepository;

	@InjectMocks
	private SigunguService sigunguService;

	@Nested
	@DisplayName("시·군·구 생성")
	class CreateSigungusTest {

		@Test
		@DisplayName("성공")
		void success() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			List<ReqCreateSigunguDto> requestDtoList = List.of(
				new ReqCreateSigunguDto("강남구", "680")
			);
			List<Sigungu> sigunguList = List.of(Sigungu.builder().sido(sido).name("강남구").code("680").build());

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.of(sido));
			given(sigunguRepository.existsByNameInAndSidoCustom(anyList(), any())).willReturn(false);
			given(sigunguRepository.existsByCodeInCustom(anyList())).willReturn(false);
			given(sigunguRepository.saveAll(anyList())).willReturn(sigunguList);

			List<ResCreateSigunguDto> responseDto = sigunguService.createSigungus(sido.getId(), requestDtoList);

			assertThat(responseDto).hasSize(1);
			then(sidoRepository).should(times(1)).findByIdCustom(any());
			then(sigunguRepository).should(times(1)).existsByNameInAndSidoCustom(anyList(), any());
			then(sigunguRepository).should(times(1)).existsByCodeInCustom(anyList());
			then(sigunguRepository).should(times(1)).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - DB에 존재하지 않는 시·도")
		void failure_notFoundSido() {
			List<ReqCreateSigunguDto> requestDtoList = List.of(
				new ReqCreateSigunguDto("강남구", "680")
			);

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.empty());

			assertThatThrownBy(() -> sigunguService.createSigungus(any(), requestDtoList))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 시/도입니다.");
			then(sigunguRepository).should(never()).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - Request에 중복된 이름 포함")
		void failure_duplicatedSigunguNameInRequest() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			List<ReqCreateSigunguDto> requestDtoList = List.of(
				new ReqCreateSigunguDto("강남구", "680"),
				new ReqCreateSigunguDto("강남구", "740")
			);

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.of(sido));

			assertThatThrownBy(() -> sigunguService.createSigungus(sido.getId(), requestDtoList))
				.isInstanceOf(RegionDuplicateRequestException.class)
				.hasMessage("요청에 중복된 시/도 이름이 포함되어 있습니다.");
			then(sigunguRepository).should(never()).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - DB에 중복된 이름 포함")
		void failure_duplicatedSigunguNameInDB() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			List<ReqCreateSigunguDto> requestDtoList = List.of(
				new ReqCreateSigunguDto("강남구", "680")
			);

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.of(sido));
			given(sigunguRepository.existsByNameInAndSidoCustom(anyList(), any())).willReturn(true);

			assertThatThrownBy(() -> sigunguService.createSigungus(sido.getId(), requestDtoList))
				.isInstanceOf(RegionAlreadyExistsException.class)
				.hasMessage("이미 존재하는 시/군/구 이름이 포함되어 있습니다.");
			then(sigunguRepository).should(never()).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - Request에 중복된 코드 포함")
		void failure_duplicatedSigunguCodeInRequest() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			List<ReqCreateSigunguDto> requestDtoList = List.of(
				new ReqCreateSigunguDto("강남구", "680"),
				new ReqCreateSigunguDto("강동구", "680")
			);

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.of(sido));
			given(sigunguRepository.existsByNameInAndSidoCustom(anyList(), any())).willReturn(false);

			assertThatThrownBy(() -> sigunguService.createSigungus(sido.getId(), requestDtoList))
				.isInstanceOf(RegionDuplicateRequestException.class)
				.hasMessage("요청에 중복된 시/도 코드가 포함되어 있습니다.");
			then(sigunguRepository).should(never()).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - DB에 중복된 코드 포함")
		void failure_duplicatedSigunguCodeInDB() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			List<ReqCreateSigunguDto> requestDtoList = List.of(
				new ReqCreateSigunguDto("강남구", "680")
			);

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.of(sido));
			given(sigunguRepository.existsByNameInAndSidoCustom(anyList(), any())).willReturn(false);
			given(sigunguRepository.existsByCodeInCustom(anyList())).willReturn(true);

			assertThatThrownBy(() -> sigunguService.createSigungus(sido.getId(), requestDtoList))
				.isInstanceOf(RegionAlreadyExistsException.class)
				.hasMessage("이미 존재하는 시/군/구 코드가 포함되어 있습니다.");
			then(sigunguRepository).should(never()).saveAll(anyList());
		}

	}

	@Nested
	@DisplayName("시·군·구 조회")
	class GetAllSigunguTest {

		@Test
		@DisplayName("성공")
		void success() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			List<Sigungu> sigunguList = List.of(
				Sigungu.builder().sido(sido).name("강남구").code("680").build(),
				Sigungu.builder().sido(sido).name("강동구").code("740").build()
			);

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.of(sido));
			given(sigunguRepository.findAllBySidoCustom(sido)).willReturn(sigunguList);

			List<ResReadSigunguDto> responseDtoList = sigunguService.getAllSigungu(sido.getId());

			assertThat(responseDtoList).hasSize(2);
			assertThat(responseDtoList.get(0).getName()).isEqualTo("강남구");
			assertThat(responseDtoList.get(0).getCode()).isEqualTo("680");
			assertThat(responseDtoList.get(1).getName()).isEqualTo("강동구");
			assertThat(responseDtoList.get(1).getCode()).isEqualTo("740");
			then(sigunguRepository).should(times(1)).findAllBySidoCustom(any());
		}

		@Test
		@DisplayName("실패 - DB에 존재하지 않는 시·도")
		void failure_notFoundSido() {
			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.empty());

			assertThatThrownBy(() -> sigunguService.getAllSigungu(any()))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 시/도입니다.");
			then(sigunguRepository).should(never()).findAllBySidoCustom(any());
		}

	}

	@Nested
	@DisplayName("시·군·구 수정")
	class UpdateSigunguTest {

		@Test
		@DisplayName("성공")
		void success() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			Sigungu sigungu = Sigungu.builder().sido(sido).name("강남구").code("680").build();
			ReqUpdateSigunguDto requestDto = new ReqUpdateSigunguDto("강동구", "740");

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.of(sido));
			given(sigunguRepository.findByIdAndSidoCustom(any(), any())).willReturn(Optional.of(sigungu));
			given(sigunguRepository.existsByNameAndSidoAndIdNotCustom(any(), any(), any())).willReturn(false);
			given(sigunguRepository.existsByCodeAndIdNotCustom(any(), any())).willReturn(false);

			ResUpdateSigunguDto responseDto = sigunguService.updateSigungu(sido.getId(), sigungu.getId(), requestDto);

			assertThat(responseDto.getName()).isEqualTo("강동구");
			assertThat(responseDto.getCode()).isEqualTo("740");
			then(sidoRepository).should(times(1)).findByIdCustom(any());
			then(sigunguRepository).should(times(1)).existsByNameAndSidoAndIdNotCustom(any(), any(), any());
			then(sigunguRepository).should(times(1)).existsByCodeAndIdNotCustom(any(), any());
		}

		@Test
		@DisplayName("실패 - DB에 존재하지 않는 시·도")
		void failure_notFoundSido() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			Sigungu sigungu = Sigungu.builder().sido(sido).name("강남구").code("680").build();
			ReqUpdateSigunguDto requestDto = new ReqUpdateSigunguDto("강동구", "740");

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.empty());

			assertThatThrownBy(() -> sigunguService.updateSigungu(sido.getId(), sigungu.getId(), requestDto))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 시/도입니다.");
			then(sigunguRepository).should(never()).findByIdAndSidoCustom(any(), any());
		}

		@Test
		@DisplayName("실패 - DB에 존재하지 않는 시·군·구")
		void failure_notFoundSigungu() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			Sigungu sigungu = Sigungu.builder().sido(sido).name("강남구").code("680").build();
			ReqUpdateSigunguDto requestDto = new ReqUpdateSigunguDto("강동구", "740");

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.of(sido));
			given(sigunguRepository.findByIdAndSidoCustom(any(), any())).willReturn(Optional.empty());

			assertThatThrownBy(() -> sigunguService.updateSigungu(sido.getId(), sigungu.getId(), requestDto))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 시/군/구입니다.");
			then(sigunguRepository).should(never()).existsByNameAndSidoAndIdNotCustom(any(), any(), any());
		}

		@Test
		@DisplayName("실패 - DB에 중복된 이름 포함")
		void failure_duplicatedSigunguNameInDB() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			Sigungu sigungu = Sigungu.builder().sido(sido).name("강남구").code("680").build();
			ReqUpdateSigunguDto requestDto = new ReqUpdateSigunguDto("강동구", "740");

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.of(sido));
			given(sigunguRepository.findByIdAndSidoCustom(any(), any())).willReturn(Optional.of(sigungu));
			given(sigunguRepository.existsByNameAndSidoAndIdNotCustom(any(), any(), any())).willReturn(true);

			assertThatThrownBy(() -> sigunguService.updateSigungu(sido.getId(), sigungu.getId(), requestDto))
				.isInstanceOf(RegionAlreadyExistsException.class)
				.hasMessage("이미 존재하는 시/군/구 이름입니다.");
			then(sigunguRepository).should(never()).existsByCodeAndIdNotCustom(any(), any());
		}

		@Test
		@DisplayName("실패 - DB에 중복된 코드 포함")
		void failure_duplicatedSigunguCodeInDB() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			Sigungu sigungu = Sigungu.builder().sido(sido).name("강남구").code("680").build();
			ReqUpdateSigunguDto requestDto = new ReqUpdateSigunguDto("강동구", "740");

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.of(sido));
			given(sigunguRepository.findByIdAndSidoCustom(any(), any())).willReturn(Optional.of(sigungu));
			given(sigunguRepository.existsByNameAndSidoAndIdNotCustom(any(), any(), any())).willReturn(false);
			given(sigunguRepository.existsByCodeAndIdNotCustom(any(), any())).willReturn(true);

			assertThatThrownBy(() -> sigunguService.updateSigungu(sido.getId(), sigungu.getId(), requestDto))
				.isInstanceOf(RegionAlreadyExistsException.class)
				.hasMessage("이미 존재하는 시/군/구 코드입니다.");
			then(sigunguRepository).should(times(1)).existsByCodeAndIdNotCustom(any(), any());
		}

	}

	@Nested
	@DisplayName("시·군·구 삭제")
	class DeleteSigunguTest {

		@Test
		@DisplayName("성공")
		void success() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			Sigungu sigungu = Sigungu.builder().sido(sido).name("강남구").code("680").build();
			Long userId = 1L;

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.of(sido));
			given(sigunguRepository.findByIdAndSidoCustom(any(), any())).willReturn(Optional.of(sigungu));

			sigunguService.deleteSigungu(sido.getId(), sigungu.getId(), userId);

			assertThat(sigungu.isDeleted()).isTrue();
			assertThat(sigungu.getDeletedAt()).isNotNull();
			assertThat(sigungu.getDeletedBy()).isEqualTo(userId);
			then(sidoRepository).should(times(1)).findByIdCustom(any());
			then(sigunguRepository).should(times(1)).findByIdAndSidoCustom(any(), any());
		}

		@Test
		@DisplayName("실패 - DB에 존재하지 않는 시·도")
		void failure_notFoundSido() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			Sigungu sigungu = Sigungu.builder().sido(sido).name("강남구").code("680").build();
			Long userId = 1L;

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.empty());

			assertThatThrownBy(() -> sigunguService.deleteSigungu(sido.getId(), sigungu.getId(), userId))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 시/도입니다.");
			then(sidoRepository).should(times(1)).findByIdCustom(any());
			then(sigunguRepository).should(never()).findByIdAndSidoCustom(any(), any());
		}

		@Test
		@DisplayName("실패 - DB에 존재하지 않는 시·군·구")
		void failure_notFoundSigungu() {
			Sido sido = Sido.builder().name("서울특별시").code("11").build();
			Sigungu sigungu = Sigungu.builder().sido(sido).name("강남구").code("680").build();
			Long userId = 1L;

			given(sidoRepository.findByIdCustom(any())).willReturn(Optional.of(sido));
			given(sigunguRepository.findByIdAndSidoCustom(any(), any())).willReturn(Optional.empty());

			assertThatThrownBy(() -> sigunguService.deleteSigungu(sido.getId(), sigungu.getId(), userId))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 시/군/구입니다.");
			then(sidoRepository).should(times(1)).findByIdCustom(any());
			then(sigunguRepository).should(times(1)).findByIdAndSidoCustom(any(), any());
		}

	}

}
