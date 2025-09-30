package com.sparta.delivery.backend.review.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateDto {

	private String context;
	private int rate;
	private UUID imageId;

}
