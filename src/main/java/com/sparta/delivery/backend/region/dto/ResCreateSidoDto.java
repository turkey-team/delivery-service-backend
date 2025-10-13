package com.sparta.delivery.backend.region.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.region.entity.Sido;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResCreateSidoDto {

	@Schema(description = "시·도 고유 ID", example = "fba4b623-1f39-425c-98ff-fe739bfbd010")
	private UUID id;

	@Schema(description = "시·도 이름", example = "서울특별시")
	private String name;

	@Schema(description = "시·도 코드", example = "11")
	private String code;

	@Schema(description = "시·도 생성 시간", example = "2025-10-09T05:11:37.650387700Z")
	private Instant createdAt;

	public static ResCreateSidoDto from(Sido sido) {
		return new ResCreateSidoDto(
			sido.getId(),
			sido.getName(),
			sido.getCode(),
			sido.getCreatedAt()
		);
	}

}
