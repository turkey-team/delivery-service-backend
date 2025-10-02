package com.sparta.delivery.backend.region.dto;

import java.util.UUID;

import com.sparta.delivery.backend.region.entity.Sido;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResReadSidoDto {

	private UUID id;

	private String name;

	private String code;

	public static ResReadSidoDto from(Sido sido) {
		return new ResReadSidoDto(
			sido.getId(),
			sido.getName(),
			sido.getCode()
		);
	}

}
