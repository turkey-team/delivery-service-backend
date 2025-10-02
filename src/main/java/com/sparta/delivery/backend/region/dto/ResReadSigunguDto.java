package com.sparta.delivery.backend.region.dto;

import java.util.UUID;

import com.sparta.delivery.backend.region.entity.Sigungu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResReadSigunguDto {

	private UUID id;

	private String name;

	private String code;

	public static ResReadSigunguDto from(Sigungu Sigungu) {
		return new ResReadSigunguDto(
			Sigungu.getId(),
			Sigungu.getName(),
			Sigungu.getCode()
		);
	}

}
