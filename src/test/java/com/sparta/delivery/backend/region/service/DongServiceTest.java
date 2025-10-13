package com.sparta.delivery.backend.region.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sparta.delivery.backend.region.dto.ReqCreateDongDto;
import com.sparta.delivery.backend.region.dto.ReqUpdateDongDto;
import com.sparta.delivery.backend.region.dto.ResCreateDongDto;
import com.sparta.delivery.backend.region.dto.ResReadDongDto;
import com.sparta.delivery.backend.region.dto.ResUpdateDongDto;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.region.exception.RegionAlreadyExistsException;
import com.sparta.delivery.backend.region.exception.RegionDuplicateRequestException;
import com.sparta.delivery.backend.region.exception.RegionNotFoundException;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.region.repository.SigunguRepository;

@ExtendWith(MockitoExtension.class)
public class DongServiceTest {

	@Mock
	private DongRepository dongRepository;

	@Mock
	private SigunguRepository sigunguRepository;

	@InjectMocks
	private DongService dongService;

	private Sido sido;
	private Sigungu sigungu;

	@BeforeEach
	void setUp() {
		sido = Sido.builder().name("서울특별시").code("11").build();
		sigungu = Sigungu.builder().sido(sido).name("강남구").code("680").build();
	}

	@Nested
	@DisplayName("동 생성")
	class CreateDongsTest {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			List<ReqCreateDongDto> requestDtoList = List.of(
				new ReqCreateDongDto("역삼동", "010")
			);
			List<Dong> dongList = List.of(Dong.builder().sigungu(sigungu).name("역삼동").code("010").build());

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.of(sigungu));
			given(dongRepository.existsByNameInAndSigunguCustom(anyList(), any())).willReturn(false);
			given(dongRepository.existsByCodeInCustom(anyList())).willReturn(false);
			given(dongRepository.saveAll(anyList())).willReturn(dongList);

			// when
			List<ResCreateDongDto> responseDtoList = dongService.createDongs(sigungu.getId(), requestDtoList);

			// then
			assertThat(responseDtoList).hasSize(1);
			then(sigunguRepository).should(times(1)).findByIdCustom(any());
			then(dongRepository).should(times(1)).existsByNameInAndSigunguCustom(anyList(), any());
			then(dongRepository).should(times(1)).existsByCodeInCustom(anyList());
			then(dongRepository).should(times(1)).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - DB에 존재하지 않는 시·군·구")
		void failure_notFoundSigungu() {
			// given
			List<ReqCreateDongDto> requestDtoList = List.of(
				new ReqCreateDongDto("역삼동", "010")
			);

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> dongService.createDongs(UUID.randomUUID(), requestDtoList))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 시/군/구입니다.");
			then(dongRepository).should(never()).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - Request에 중복된 이름 포함")
		void failure_duplicatedDongNameInRequest() {
			// given
			List<ReqCreateDongDto> requestDtoList = List.of(
				new ReqCreateDongDto("역삼동", "010"),
				new ReqCreateDongDto("역삼동", "030")
			);

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.of(sigungu));

			// when & then
			assertThatThrownBy(() -> dongService.createDongs(sigungu.getId(), requestDtoList))
				.isInstanceOf(RegionDuplicateRequestException.class)
				.hasMessage("요청에 중복된 동 이름이 포함되어 있습니다.");
			then(dongRepository).should(never()).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - DB에 중복된 이름 포함")
		void failure_duplicatedDongNameInDB() {
			// given
			List<ReqCreateDongDto> requestDtoList = List.of(
				new ReqCreateDongDto("역삼동", "010")
			);

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.of(sigungu));
			given(dongRepository.existsByNameInAndSigunguCustom(anyList(), any())).willReturn(true);

			// when & then
			assertThatThrownBy(() -> dongService.createDongs(sigungu.getId(), requestDtoList))
				.isInstanceOf(RegionAlreadyExistsException.class)
				.hasMessage("이미 존재하는 동 이름이 포함되어 있습니다.");
			then(dongRepository).should(never()).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - Request에 중복된 코드 포함")
		void failure_duplicatedDongCodeInRequest() {
			// when & then
			List<ReqCreateDongDto> requestDtoList = List.of(
				new ReqCreateDongDto("역삼동", "010"),
				new ReqCreateDongDto("삼성동", "010")
			);

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.of(sigungu));
			given(dongRepository.existsByNameInAndSigunguCustom(anyList(), any())).willReturn(false);

			// when & then
			assertThatThrownBy(() -> dongService.createDongs(sigungu.getId(), requestDtoList))
				.isInstanceOf(RegionDuplicateRequestException.class)
				.hasMessage("요청에 중복된 동 코드가 포함되어 있습니다.");
			then(dongRepository).should(never()).saveAll(anyList());
		}

		@Test
		@DisplayName("실패 - DB에 중복된 코드 포함")
		void failure_duplicatedDongCodeInDB() {
			// given
			List<ReqCreateDongDto> requestDtoList = List.of(
				new ReqCreateDongDto("역삼동", "010")
			);

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.of(sigungu));
			given(dongRepository.existsByNameInAndSigunguCustom(anyList(), any())).willReturn(false);
			given(dongRepository.existsByCodeInCustom(anyList())).willReturn(true);

			// when & then
			assertThatThrownBy(() -> dongService.createDongs(sigungu.getId(), requestDtoList))
				.isInstanceOf(RegionAlreadyExistsException.class)
				.hasMessage("이미 존재하는 동 코드가 포함되어 있습니다.");
			then(dongRepository).should(never()).saveAll(anyList());
		}

	}

	@Nested
	@DisplayName("동 조회")
	class GetAllDongTest {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			List<Dong> dongList = List.of(
				Dong.builder().sigungu(sigungu).name("역삼동").code("010").build(),
				Dong.builder().sigungu(sigungu).name("삼성동").code("030").build()
			);

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.of(sigungu));
			given(dongRepository.findAllBySigunguCustom(sigungu)).willReturn(dongList);

			// when
			List<ResReadDongDto> responseDtoList = dongService.getAllDong(sigungu.getId());

			// then
			assertThat(responseDtoList).hasSize(2);
			assertThat(responseDtoList.get(0).getName()).isEqualTo("역삼동");
			assertThat(responseDtoList.get(0).getCode()).isEqualTo("010");
			assertThat(responseDtoList.get(1).getName()).isEqualTo("삼성동");
			assertThat(responseDtoList.get(1).getCode()).isEqualTo("030");
			then(dongRepository).should(times(1)).findAllBySigunguCustom(any());
		}

		@Test
		@DisplayName("실패 - DB에 존재하지 않는 시·군·구")
		void failure_notFoundSigungu() {
			// given
			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> dongService.getAllDong(sigungu.getId()))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 시/군/구입니다.");
			then(sigunguRepository).should(times(1)).findByIdCustom(any());
			then(dongRepository).should(never()).findAllBySigunguCustom(any());
		}

	}

	@Nested
	@DisplayName("동 수정")
	class UpdateDongTest {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			Dong dong = Dong.builder().sigungu(sigungu).name("역삼동").code("010").build();
			ReqUpdateDongDto requestDto = new ReqUpdateDongDto("삼성동", "030");

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.of(sigungu));
			given(dongRepository.findByIdAndSigunguCustom(any(), any())).willReturn(Optional.of(dong));
			given(dongRepository.existsByNameAndSigunguAndIdNotCustom(any(), any(), any())).willReturn(false);
			given(dongRepository.existsByCodeAndIdNotCustom(any(), any())).willReturn(false);

			// when
			ResUpdateDongDto responseDto = dongService.updateDong(sigungu.getId(), dong.getId(), requestDto);

			// then
			assertThat(responseDto.getName()).isEqualTo("삼성동");
			assertThat(responseDto.getCode()).isEqualTo("030");
			then(sigunguRepository).should(times(1)).findByIdCustom(any());
			then(dongRepository).should(times(1)).findByIdAndSigunguCustom(any(), any());
			then(dongRepository).should(times(1)).existsByNameAndSigunguAndIdNotCustom(any(), any(), any());
			then(dongRepository).should(times(1)).existsByCodeAndIdNotCustom(any(), any());
		}

		@Test
		@DisplayName("실패 - DB에 존재하지 않는 시·군·구")
		void failure_notFoundSigungu() {
			// given
			ReqUpdateDongDto requestDto = new ReqUpdateDongDto("역삼동", "010");

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> dongService.updateDong(UUID.randomUUID(), UUID.randomUUID(), requestDto))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 시/군/구입니다.");
			then(sigunguRepository).should(times(1)).findByIdCustom(any());
		}

		@Test
		@DisplayName("실패 - DB에 존재하지 않는 동")
		void failure_notFoundDong() {
			// given
			ReqUpdateDongDto requestDto = new ReqUpdateDongDto("역삼동", "010");

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.of(sigungu));
			given(dongRepository.findByIdAndSigunguCustom(any(), any())).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> dongService.updateDong(sigungu.getId(), UUID.randomUUID(), requestDto))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 동입니다.");
			then(sigunguRepository).should(times(1)).findByIdCustom(any());
			then(dongRepository).should(times(1)).findByIdAndSigunguCustom(any(), any());
		}

		@Test
		@DisplayName("실패 - DB에 중복된 이름 포함")
		void failure_duplicatedDongNameInDB() {
			// given
			Dong dong = Dong.builder().sigungu(sigungu).name("역삼동").code("010").build();
			ReqUpdateDongDto requestDto = new ReqUpdateDongDto("삼성동", "030");

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.of(sigungu));
			given(dongRepository.findByIdAndSigunguCustom(any(), any())).willReturn(Optional.of(dong));
			given(dongRepository.existsByNameAndSigunguAndIdNotCustom(any(), any(), any())).willReturn(true);

			// when & then
			assertThatThrownBy(() -> dongService.updateDong(sigungu.getId(), dong.getId(), requestDto))
				.isInstanceOf(RegionAlreadyExistsException.class)
				.hasMessage("이미 존재하는 동 이름입니다.");
			then(sigunguRepository).should(times(1)).findByIdCustom(any());
			then(dongRepository).should(times(1)).findByIdAndSigunguCustom(any(), any());
			then(dongRepository).should(times(1)).existsByNameAndSigunguAndIdNotCustom(any(), any(), any());
		}

		@Test
		@DisplayName("실패 - DB에 중복된 코드 포함")
		void failure_duplicatedDongCodeInDB() {
			// given
			Dong dong = Dong.builder().sigungu(sigungu).name("역삼동").code("010").build();
			ReqUpdateDongDto requestDto = new ReqUpdateDongDto("삼성동", "030");

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.of(sigungu));
			given(dongRepository.findByIdAndSigunguCustom(any(), any())).willReturn(Optional.of(dong));
			given(dongRepository.existsByNameAndSigunguAndIdNotCustom(any(), any(), any())).willReturn(false);
			given(dongRepository.existsByCodeAndIdNotCustom(any(), any())).willReturn(true);

			// when & then
			assertThatThrownBy(() -> dongService.updateDong(sigungu.getId(), dong.getId(), requestDto))
				.isInstanceOf(RegionAlreadyExistsException.class)
				.hasMessage("이미 존재하는 동 코드입니다.");
			then(sigunguRepository).should(times(1)).findByIdCustom(any());
			then(dongRepository).should(times(1)).findByIdAndSigunguCustom(any(), any());
			then(dongRepository).should(times(1)).existsByNameAndSigunguAndIdNotCustom(any(), any(), any());
			then(dongRepository).should(times(1)).existsByCodeAndIdNotCustom(any(), any());
		}

	}

	@Nested
	@DisplayName("동 삭제")
	class DeleteDongTest {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			Dong dong = Dong.builder().sigungu(sigungu).name("역삼동").code("010").build();
			Long userId = 1L;

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.of(sigungu));
			given(dongRepository.findByIdAndSigunguCustom(any(), any())).willReturn(Optional.of(dong));

			// when
			dongService.deleteDong(sigungu.getId(), dong.getId(), userId);

			// then
			assertThat(dong.isDeleted()).isTrue();
			assertThat(dong.getDeletedAt()).isNotNull();
			assertThat(dong.getDeletedBy()).isEqualTo(userId);
			then(sigunguRepository).should(times(1)).findByIdCustom(any());
			then(dongRepository).should(times(1)).findByIdAndSigunguCustom(any(), any());
		}

		@Test
		@DisplayName("실패 - DB에 존재하지 않는 시·군·구")
		void failure_notFoundSigungu() {
			// given
			Dong dong = Dong.builder().sigungu(sigungu).name("역삼동").code("010").build();
			Long userId = 1L;

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> dongService.deleteDong(sigungu.getId(), dong.getId(), userId))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 시/군/구입니다.");
			then(sigunguRepository).should(times(1)).findByIdCustom(any());
			then(dongRepository).should(never()).findByIdAndSigunguCustom(any(), any());
		}

		@Test
		@DisplayName("실패 - DB에 존재하지 않는 동")
		void failure_notFoundDong() {
			// given
			Dong dong = Dong.builder().sigungu(sigungu).name("역삼동").code("010").build();
			Long userId = 1L;

			given(sigunguRepository.findByIdCustom(any())).willReturn(Optional.of(sigungu));
			given(dongRepository.findByIdAndSigunguCustom(any(), any())).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> dongService.deleteDong(sigungu.getId(), dong.getId(), userId))
				.isInstanceOf(RegionNotFoundException.class)
				.hasMessage("존재하지 않는 동입니다.");
			then(sigunguRepository).should(times(1)).findByIdCustom(any());
			then(dongRepository).should(times(1)).findByIdAndSigunguCustom(any(), any());
		}

	}

}
