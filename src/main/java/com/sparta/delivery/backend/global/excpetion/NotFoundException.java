package com.sparta.delivery.backend.global.excpetion;

public class NotFoundException extends RuntimeException {
	public NotFoundException(String message) {
		super(message);
	}
}
