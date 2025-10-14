package com.sparta.delivery.backend.store.dto;

import com.sparta.delivery.backend.store.entity.StoreStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ReqUpdateStoreStatusDto", description = "가게 상태 수정 요청 DTO")
public class ReqUpdateStoreStatusDto {
	@NotBlank(message = "가게 상태를 설정해주세요")
	@Schema(description = "가게 상태 설정", example = "CLOSED", required = true)
	private StoreStatusEnum storeStatus;
}
