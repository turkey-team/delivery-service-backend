package com.sparta.delivery.backend.review.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ReviewRegisterDto {

	private String context;
	private int rate;
	private UUID imageId;

}
