package com.sparta.delivery.backend.region.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sparta.delivery.backend.region.dto.ReqCreateSidoDto;
import com.sparta.delivery.backend.region.dto.ReqUpdateSidoDto;
import com.sparta.delivery.backend.region.dto.ResCreateSidoDto;
import com.sparta.delivery.backend.region.dto.ResReadSidoDto;
import com.sparta.delivery.backend.region.dto.ResUpdateSidoDto;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.exception.RegionAlreadyExistsException;
import com.sparta.delivery.backend.region.exception.RegionDuplicateRequestException;
import com.sparta.delivery.backend.region.exception.RegionNotFoundException;
import com.sparta.delivery.backend.region.repository.SidoRepository;

@ExtendWith(MockitoExtension.class)
public class SidoServiceTest {

	@Mock
	private SidoRepository sidoRepository;

	@InjectMocks
	private SidoService sidoService;

	@Nested
	@DisplayName("시·도 생성")
	class CreateSidosTest {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			List<ReqCreateSidoDto> requestDtoList = List.of(
				new ReqCreateSidoDto("이름1", "11")
			);
			List<Sido> sidoList = List.of(Sido.builder().name("이름1").code("11").build());

			given(sidoRepository.existsByNameInCustom(anyList())).willReturn(false);
			given(sidoRepository.existsByCodeInCustom(anyList())).willReturn(false);
			given(sidoRepository.saveAll(anyList())).willReturn(sidoList);

			// when
			List<ResCreateSidoDto> responseDtoList = sidoService.createSidos(requestDtoList);

			// then
			assertThat(responseDtoList).hasSize(1);
			then(sidoRepository).should(times(1)).existsByNameInCustom(anyList());
			then(sidoRepository).should(times(1)).existsByCodeInCustom(anyList());
			then(sidoRepository).should(times(1)).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - Request에 중복된 이름 포함")
		void failure_duplicatedSidoNameInRequest() {
			// given
			List<ReqCreateSidoDto> requestDtoList = List.of(
				new ReqCreateSidoDto("이름1", "11"),
				new ReqCreateSidoDto("이름1", "12")
			);

			// when & then
			assertThatThrownBy(() -> sidoService.createSidos(requestDtoList))
				.isInstanceOf(RegionDuplicateRequestException.class)
				.hasMessage("요청에 중복된 시/도 이름이 포함되어 있습니다.");
			then(sidoRepository).should(never()).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - DB에 중복된 이름 포함")
		void failure_duplicatedSidoNameInDB() {
			// given
			List<ReqCreateSidoDto> requestDtoList = List.of(
				new ReqCreateSidoDto("이름1", "11")
			);

			given(sidoRepository.existsByNameInCustom(anyList())).willReturn(true);

			// when & then
			assertThatThrownBy(() -> sidoService.createSidos(requestDtoList))
				.isInstanceOf(RegionAlreadyExistsException.class)
				.hasMessage("이미 존재하는 시/도 이름이 포함되어 있습니다.");
			then(sidoRepository).should(times(1)).existsByNameInCustom(anyList());
			then(sidoRepository).should(never()).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - Request에 중복된 코드 포함")
		void failure_duplicatedSidoCodeInRequest() {
			// given
			List<ReqCreateSidoDto> requestDtoList = List.of(
				new ReqCreateSidoDto("이름1", "11"),
				new ReqCreateSidoDto("이름2", "11")
			);

			// when & then
			assertThatThrownBy(() -> sidoService.createSidos(requestDtoList))
				.isInstanceOf(RegionDuplicateRequestException.class)
				.hasMessage("요청에 중복된 시/도 코드가 포함되어 있습니다.");
			then(sidoRepository).should(never()).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - DB에 중복된 코드 포함")
		void failure_duplicatedSidoCodeInDB() {
			// given
			List<ReqCreateSidoDto> requestDtoList = List.of(
				new ReqCreateSidoDto("이름1", "11")
			);

			given(sidoRepository.existsByCodeInCustom(anyList())).willReturn(true);

			// when & then
			assertThatThrownBy(() -> sidoService.createSidos(requestDtoList))
				.isInstanceOf(RegionAlreadyExistsException.class)
				.hasMessage("이미 존재하는 시/도 코드가 포함되어 있습니다.");
			then(sidoRepository).should(times(1)).existsByCodeInCustom(anyList());
			then(sidoRepository).should(never()).saveAll(anyList());
		}

	}

	@Nested
	@DisplayName("시·도 조회")
	class GetAllSidoTest {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			List<Sido> sidoList = List.of(
				Sido.builder().name("이름1").code("11").build(),
				Sido.builder().name("이름2").code("12").build()
			);

			given(sidoRepository.findAllCustom()).willReturn(sidoList);

			// when
			List<ResReadSidoDto> responseDto = sidoService.getAllSido();

			// then
			assertThat(responseDto).hasSize(2);
			assertThat(responseDto.get(0).getName()).isEqualTo("이름1");
			assertThat(responseDto.get(0).getCode()).isEqualTo("11");
			assertThat(responseDto.get(1).getName()).isEqualTo("이름2");
			assertThat(responseDto.get(1).getCode()).isEqualTo("12");
			then(sidoRepository).should(times(1)).findAllCustom();
		}

	}

	@Nested
	@DisplayName("시·도 수정")
	class UpdateSidoTest {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			Sido sido = Sido.builder().name("이름1").code("11").build();
			ReqUpdateSidoDto requestDto = new ReqUpdateSidoDto("이름2", "12");
			UUID sidoId = UUID.randomUUID();

			given(sidoRepository.findByIdCustom(sidoId)).willReturn(Optional.of(sido));
			given(sidoRepository.existsByNameAndIdNotCustom(requestDto.getName(), sidoId)).willReturn(false);
			given(sidoRepository.existsByCodeAndIdNotCustom(requestDto.getCode(), sidoId)).willReturn(false);

			// when
			ResUpdateSidoDto responseDto = sidoService.updateSido(sidoId, requestDto);

			// then
			assertThat(responseDto.getName()).isEqualTo("이름2");
			assertThat(responseDto.getCode()).isEqualTo("12");
			then(sidoRepository).should(times(1)).findByIdCustom(any());
			then(sidoRepository).should(times(1)).existsByNameAndIdNotCustom(requestDto.getName(), sidoId);
			then(sidoRepository).should(times(1)).existsByCodeAndIdNotCustom(requestDto.getCode(), sidoId);
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 시·도")
		void failure_notFoundSido() {
			// given
			ReqUpdateSidoDto requestDto = new ReqUpdateSidoDto("이름2", "12");
			UUID sidoId = UUID.randomUUID();

			given(sidoRepository.findByIdCustom(sidoId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> sidoService.updateSido(sidoId, requestDto))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 시/도입니다.");
			then(sidoRepository).should(times(1)).findByIdCustom(any());
			then(sidoRepository).should(never()).existsByNameAndIdNotCustom(requestDto.getName(), sidoId);
			then(sidoRepository).should(never()).existsByCodeAndIdNotCustom(requestDto.getCode(), sidoId);
		}

		@Test
		@DisplayName("실패 - DB에 중복된 이름 존재")
		void failure_duplicatedSidoNameInDB() {
			// given
			Sido sido = Sido.builder().name("이름1").code("11").build();
			ReqUpdateSidoDto requestDto = new ReqUpdateSidoDto("이름2", "12");
			UUID sidoId = UUID.randomUUID();

			given(sidoRepository.findByIdCustom(sidoId)).willReturn(Optional.of(sido));
			given(sidoRepository.existsByNameAndIdNotCustom(requestDto.getName(), sidoId)).willReturn(true);

			// when & then
			assertThatThrownBy(() -> sidoService.updateSido(sidoId, requestDto))
				.isInstanceOf(RegionAlreadyExistsException.class)
				.hasMessage("이미 존재하는 시/도 이름입니다.");
			then(sidoRepository).should(times(1)).findByIdCustom(any());
			then(sidoRepository).should(times(1)).existsByNameAndIdNotCustom(requestDto.getName(), sidoId);
			then(sidoRepository).should(never()).existsByCodeAndIdNotCustom(requestDto.getCode(), sidoId);
		}

		@Test
		@DisplayName("실패 - DB에 중복된 코드 존재")
		void failure_duplicatedSidoCodeInDB() {
			// given
			Sido sido = Sido.builder().name("이름1").code("11").build();
			ReqUpdateSidoDto requestDto = new ReqUpdateSidoDto("이름2", "12");
			UUID sidoId = UUID.randomUUID();

			given(sidoRepository.findByIdCustom(sidoId)).willReturn(Optional.of(sido));
			given(sidoRepository.existsByNameAndIdNotCustom(requestDto.getName(), sidoId)).willReturn(false);
			given(sidoRepository.existsByCodeAndIdNotCustom(requestDto.getCode(), sidoId)).willReturn(true);

			// when & then
			assertThatThrownBy(() -> sidoService.updateSido(sidoId, requestDto))
				.isInstanceOf(RegionAlreadyExistsException.class)
				.hasMessage("이미 존재하는 시/도 코드입니다.");
			then(sidoRepository).should(times(1)).findByIdCustom(sidoId);
			then(sidoRepository).should(times(1)).existsByNameAndIdNotCustom(requestDto.getName(), sidoId);
			then(sidoRepository).should(times(1)).existsByCodeAndIdNotCustom(requestDto.getCode(), sidoId);
		}

	}

	@Nested
	@DisplayName("시·도 삭제")
	class DeleteSidoTest {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			Sido sido = Sido.builder().name("이름1").code("11").build();
			UUID sidoId = UUID.randomUUID();
			Long userId = 1L;

			given(sidoRepository.findByIdCustom(any(UUID.class))).willReturn(Optional.of(sido));

			// when
			sidoService.deleteSido(sidoId, userId);

			// then
			assertThat(sido.getDeletedAt()).isNotNull();
			assertThat(sido.getDeletedBy()).isEqualTo(1L);
			then(sidoRepository).should(times(1)).findByIdCustom(any());
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 시·도")
		void failure_notFoundSido() {
			// given
			UUID sidoId = UUID.randomUUID();
			Long userId = 1L;
			given(sidoRepository.findByIdCustom(sidoId)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> sidoService.deleteSido(sidoId, userId))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 시/도입니다.");
			then(sidoRepository).should(times(1)).findByIdCustom(any());
		}

	}

}
