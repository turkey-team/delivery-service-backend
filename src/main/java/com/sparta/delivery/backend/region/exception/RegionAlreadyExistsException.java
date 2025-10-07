package com.sparta.delivery.backend.region.exception;

public class RegionAlreadyExistsException extends RuntimeException {
	public RegionAlreadyExistsException(String message) {
		super(message);
	}
}
