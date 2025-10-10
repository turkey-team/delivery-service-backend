package com.sparta.delivery.backend.region.exception;

public class RegionDuplicateRequestException extends RuntimeException {
	public RegionDuplicateRequestException(String message) {
		super(message);
	}
}
