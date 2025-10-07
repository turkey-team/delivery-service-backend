package com.sparta.delivery.backend.region.exception;

public class RegionNotFoundException extends RuntimeException {
	public RegionNotFoundException(String message) {
		super(message);
	}
}
