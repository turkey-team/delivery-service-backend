package com.sparta.delivery.backend.store.dto;

import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateStoreDto {
	@NotBlank(message = "가게 이름은 필수로 입력해주세요.")
	private String name;

	@NotBlank(message = "가게 주소는 필수로 입력해주세요.")
	private String regionCode;

	@NotBlank(message = "가게 주소는 필수로 입력해주세요.")
	private String addressDetail;

	@NotBlank(message = "가게 연락처는 필수로 입력해주세요.")
	private String phoneNumber;

	private int deliveryFee;

	private Integer minOrderPrice;

	@NotEmpty(message = "카테고리는 필수로 선택해주세요.")
	private List<UUID> categories;

	@NotEmpty(message = "사업자등록증과 가게 사진을 첨부해주세요.")
	private List<ImageDto> images;

	@Getter
	public static class ImageDto{
		@NotBlank
		private String url;

		@NotBlank
		private String type;
		// store : 가게사진
		// business : 사업자등록증
	}

	private UUID ownerId;

	//details
	private String description;
	@NotBlank(message = "영업시간을 설정해주세요.")
	private String operatingHours;
	private String holiday;
	@NotBlank(message = "사업자등록증번호는 필수로 입력해주세요.")
	@Length(min=10,max = 12)
	private String businessNumber;

}
