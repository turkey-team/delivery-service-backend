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
@NoArgsConstructor
@AllArgsConstructor
public class ReqUpdateStoreInfoDto {

	//store
	@NotBlank(message = "가게 이름은 비워둘 수 없습니다.")
	private String storeName;

	@NotBlank(message = "상세주소는 필수입니다.")
	private String addressDetails;

	@NotBlank(message = "연락처는 필수입니다.")
	@Length(min = 11, max = 15)
	private String phoneNumber;

	@NotBlank(message = "주소지 입력은 필수입니다.")

	@Length(min = 3, max = 3)
	private String regionDong;

	@NotEmpty(message = "카테고리 선택은 필수입니다.")
	private List<UUID> categories;

	@NotEmpty(message = "가게 사진을 첨부해주세요.")
	private List<ImageDto> images;

	@Getter
	public static class ImageDto {
		private UUID imageId;

		@NotBlank
		private String url;

		@NotBlank
		private String type;
		// 배민 정책 참고하면 사업자번호 변경은 고객센터 통한 요청 필요 -> 사업자등록증 이미지 변경 불가
	}

}
