package com.sparta.delivery.backend.region.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.region.entity.Sido;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResUpdateSidoDto {

	private UUID id;

	private String name;

	private String code;

	private Instant updatedAt;

	public static ResUpdateSidoDto from(Sido sido) {
		return new ResUpdateSidoDto(
			sido.getId(),
			sido.getName(),
			sido.getCode(),
			sido.getUpdatedAt()
		);
	}

}
