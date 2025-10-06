package com.sparta.delivery.backend.store.dto;

import com.sparta.delivery.backend.store.entity.StoreStatusEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqUpdateStoreStatusDto {
	@NotBlank(message = "가게 상태를 설정해주세요")
	private StoreStatusEnum storeStatus;
}
