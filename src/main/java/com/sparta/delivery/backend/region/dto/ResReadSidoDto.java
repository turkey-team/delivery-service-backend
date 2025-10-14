package com.sparta.delivery.backend.region.dto;

import java.util.UUID;

import com.sparta.delivery.backend.region.entity.Sido;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResReadSidoDto {

	@Schema(description = "시·도 고유 ID", example = "fba4b623-1f39-425c-98ff-fe739bfbd010")
	private UUID id;

	@Schema(description = "시·도 이름", example = "서울특별시")
	private String name;

	@Schema(description = "시·도 코드", example = "11")
	private String code;

	public static ResReadSidoDto from(Sido sido) {
		return new ResReadSidoDto(
			sido.getId(),
			sido.getName(),
			sido.getCode()
		);
	}

}
