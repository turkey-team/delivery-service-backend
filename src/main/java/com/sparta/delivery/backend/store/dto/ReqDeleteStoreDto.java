package com.sparta.delivery.backend.store.dto;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ReqDeleteStoreDto", description = "가게 삭제 요청 DTO")
public class ReqDeleteStoreDto {
	@NotBlank
	@Length(min = 10, max = 12)
	@Schema(description = "삭제할 가게 사업자번호", example = "1234567890")
	private String businessNumber;
}
