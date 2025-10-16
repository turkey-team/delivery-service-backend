package com.sparta.delivery.backend.store.dto;

import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ReqUpdateStoreInfoDto", description = "가게 정보 수정 요청 DTO")
public class ReqUpdateStoreInfoDto {

	//store
	@NotBlank(message = "가게 이름은 비워둘 수 없습니다.")
	@Schema(description = "가게 이름", example = "김밥천국 광화문점", required = true)
	private String storeName;

	@NotBlank(message = "상세주소는 필수입니다.")
	@Schema(description = "가게 상세주소", example = "광화문로 1길 11", required = true)
	private String addressDetails;

	@NotBlank(message = "연락처는 필수입니다.")
	@Length(min = 11, max = 15)
	@Schema(description = "가게 연락처", example = "02-9874-6521", required = true)
	private String phoneNumber;

	@NotBlank(message = "주소지 입력은 필수입니다.")
	@Length(min = 3, max = 3)
	@Schema(description = "법정동 코드", example = "123", required = true)
	private String regionDong;

	@NotEmpty(message = "카테고리 선택은 필수입니다.")
	@Schema(description = "가게 카테고리 UUID 리스트",
		example = "[\"123e4567-e89b-12d3-a456-426614174000\", \"223e4567-e89b-12d3-a456-426614174001\"]",
		required = true)
	private List<UUID> categories;

	@DecimalMin(value = "-180.0", message = "경도는 -180 이상 ~ 180 이하이어야 합니다.")
	@DecimalMax(value = "180.0", message = "경도는 -180 이상 ~ 180 이하이어야 합니다.")
	@Schema(description = "경도", example = "126.970335", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "경도는 필수입니다.")
	private Double longitude;

	@DecimalMin(value = "-90.0", message = "위도는 -90 이상 ~ 90 이하이어야 합니다.")
	@DecimalMax(value = "90.0", message = "위도는 -90 이상 ~ 90 이하이어야 합니다.")
	@Schema(description = "위도", example = "37.574352", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "위도는 필수입니다.")
	private Double latitude;

	@Schema(description = "가게 사진 리스트, 이미지", required = true,
		example = "[{\"imageId\": \"123e4567-e89b-12d3-a456-426614174000\""
			+ ", \"url\": \"xxx.png\", \"type\": \"store\"}"
			+ ", {\"imageId\": \"223e4567-e89b-12d3-a456-426614174001\""
			+ ", \"url\": \"yyy.png\", \"type\": \"store\"}]")
	@NotEmpty(message = "가게 사진을 첨부해주세요.")
	@Valid
	private List<ImageDto> images;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ImageDto {
		@Schema(description = "이미지 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
		private UUID imageId;

		@NotBlank
		@Schema(description = "이미지 URL", example = "image.png", required = true)
		private String url;

		@NotBlank
		@Pattern(regexp = "^(store)$", message = "이미지는 가게사진만 수정 가능합니다.")
		@Schema(description = "이미지 타입", example = "store", required = true)
		private String type;
		// 배민 정책 참고하면 사업자번호 변경은 고객센터 통한 요청 필요 -> 사업자등록증 이미지 변경 불가
	}

}
