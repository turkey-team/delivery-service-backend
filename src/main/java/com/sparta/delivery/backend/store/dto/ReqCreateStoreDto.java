package com.sparta.delivery.backend.store.dto;

import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ReqCreateStoreDto", description = "가게 생성 요청 DTO")
public class ReqCreateStoreDto {
	@NotBlank(message = "가게 이름은 필수로 입력해주세요.")
	@Schema(description = "가게명", example = "김밥천국", required = true)
	private String name;

	@NotBlank(message = "가게 주소는 필수로 입력해주세요.")
	@Schema(description = "법정동 코드", example = "123", required = true)
	private String regionCode;

	@NotBlank(message = "가게 주소는 필수로 입력해주세요.")
	@Schema(description = "가게 상세 주소", example = "광화문로 1번길 1", required = true)
	private String addressDetail;

	@NotBlank(message = "가게 연락처는 필수로 입력해주세요.")
	@Schema(description = "가게 연락처", example = "02-1234-5678", required = true)
	private String phoneNumber;

	@Schema(description = "배달료", example = "15000")
	private int deliveryFee;

	@Schema(description = "최소주문금액", example = "120000")
	private Integer minOrderPrice;

	@NotEmpty(message = "카테고리는 필수로 선택해주세요.")
	@Schema(    description = "가게 카테고리 UUID 리스트",
		example = "[\"123e4567-e89b-12d3-a456-426614174000\", \"223e4567-e89b-12d3-a456-426614174001\"]",
		required = true)
	private List<UUID> categories;

	@NotEmpty(message = "사업자등록증과 가게 사진을 첨부해주세요.")
	@Schema(description = "사업자등록증, 이미지"
		, example = "[{\"url\": \"xxx.png\", \"type\": \"business\"}, {\"url\": \"yyy.png\", \"type\": \"store\"}]"
		, required = true)
	@Valid
	private List<ImageDto> images;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ImageDto{
		@NotBlank
		@Schema(description = "이미지 URL", example = "xxx.png")
		private String url;

		@NotBlank
		@Pattern(regexp = "^(store|business)$", message = "이미지는 가게사진과 사업자등록증만 가능합니다.")
		@Schema(description = "이미지 타입[store, business]", example = "business", allowableValues = {"store", "business"})
		private String type;

	}

	@Schema(description = "manager가 가게 등록시 필요한 OwnerUUID", example = "123e4567-e89b-12d3-a456-426614174000")
	private UUID ownerId;

	//details
	@Schema(description = "가게 설명", example = "광화문 김밥천국입니다.")
	private String description;
	@NotBlank(message = "영업시간을 설정해주세요.")
	@Schema(description = "가게 운영시간", example = "08:00 ~ 20:00", required = true)
	private String operatingHours;
	@Schema(description = "가게 휴업일", example = "일요일")
	private String holiday;
	@NotBlank(message = "사업자등록증번호는 필수로 입력해주세요.")
	@Length(min=10,max = 12)
	@Schema(description = "사업자등록번호", example = "1234567890", required = true)
	private String businessNumber;

}
