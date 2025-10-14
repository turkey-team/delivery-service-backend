package com.sparta.delivery.backend.store.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ResCreateStoreDto", description = "가게 생성 응답 DTO")
public class ResCreateStoreDto {
	@Schema(description = "생성된 Store Id", example = "123e4567-e89b-12d3-a456-426614174000")
	private UUID id;
	@Schema(description = "생성된 가게명", example = "김밥천국")
	private String name;

	public ResCreateStoreDto(UUID id, String name) {
		this.id = id;
		this.name = name;
	}

}
