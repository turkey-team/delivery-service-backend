package com.sparta.delivery.backend.ai.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.sparta.delivery.backend.ai.dto.ReqCreateAiPromptDto;
import com.sparta.delivery.backend.ai.dto.ResCreateAiPromptDto;
import com.sparta.delivery.backend.ai.dto.ResReadAiPromptDto;
import com.sparta.delivery.backend.ai.entity.AiPrompt;
import com.sparta.delivery.backend.ai.repository.AiPromptRepository;

@ExtendWith(MockitoExtension.class)
public class AiPromptServiceTest {

	@Mock
	private AiPromptRepository aiPromptRepository;

	@Mock
	private GoogleAiService googleAiService;

	@InjectMocks
	private AiPromptService aiPromptService;

	@Nested
	@DisplayName("프롬프트 생성 테스트")
	class createAiPrompt {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			ReqCreateAiPromptDto requestDto = new ReqCreateAiPromptDto("요청");
			AiPrompt aiPrompt = AiPrompt.builder()
				.reqMessage("요청")
				.resMessage("응답")
				.build();

			given(googleAiService.createAiPrompt("요청")).willReturn("응답");
			given(aiPromptRepository.save(any(AiPrompt.class))).willReturn(aiPrompt);

			// when
			ResCreateAiPromptDto responseDto = aiPromptService.createAiPrompt(requestDto);

			// then
			assertThat(responseDto.getResMessage()).isEqualTo("응답");
			then(googleAiService).should(times(1)).createAiPrompt("요청");
			then(aiPromptRepository).should(times(1)).save(any(AiPrompt.class));
		}

	}

	@Nested
	@DisplayName("프롬프트 조회 테스트")
	class getAllAiPrompts {

		@Test
		@DisplayName("성공")
		void success() {
			// given
			AiPrompt aiPrompt = AiPrompt.builder()
				.reqMessage("요청")
				.resMessage("응답")
				.build();
			Pageable pageable = PageRequest.of(0, 10);
			Page<AiPrompt> page = new PageImpl<>(List.of(aiPrompt));

			given(aiPromptRepository.findAll(pageable)).willReturn(page);

			// when
			Page<ResReadAiPromptDto> responseDto = aiPromptService.getAllAiPrompts(pageable);

			// then
			assertThat(responseDto).hasSize(1);
			assertThat(responseDto.getContent().get(0).getReqMessage()).isEqualTo("요청");
			assertThat(responseDto.getContent().get(0).getResMessage()).isEqualTo("응답");
			then(aiPromptRepository).should(times(1)).findAll(pageable);
		}

	}

}
