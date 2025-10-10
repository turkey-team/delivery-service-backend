package com.sparta.delivery.backend.store.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqDeleteStoreDto {
	@NotBlank
	@Length(min = 10, max = 12)
	private String businessNumber;
}
