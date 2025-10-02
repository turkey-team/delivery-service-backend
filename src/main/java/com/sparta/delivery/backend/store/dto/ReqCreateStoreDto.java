package com.sparta.delivery.backend.store.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateStoreDto {
	@NotNull(message = "가게 이름은 필수로 입력해주세요.")
	private String name;

	@NotNull(message = "가게 주소는 필수로 입력해주세요.")
	private String regionCode;

	@NotNull(message = "가게 주소는 필수로 입력해주세요.")
	private String addressDetail;

	@NotNull(message = "가게 연락처는 필수로 입력해주세요.")
	private String phoneNumber;

	@NotNull(message = "배달료를 비워두실 수 없습니다. 없다면 0을 입력하세요.")
	private int deliveryFee;

	private Integer minOrderPrice;

	@NotEmpty(message = "카테고리는 필수로 선택해주세요.")
	private List<UUID> categories;

	@NotEmpty(message = "이미지를 첨부해주세요.")
	private Map<String, List<String>> images;
	// store : 가게
	// business : 사업자등록증

	//details
	private String description;
	private String operatingHours;
	private String holiday;
	@NotNull(message = "사업자등록증번호는 필수로 입력해주세요.")
	private String businessNumber;

}
