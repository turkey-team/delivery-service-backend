package com.sparta.delivery.backend.global.excpetion;

public class ExternalApiTimeoutException extends RuntimeException {
	public ExternalApiTimeoutException(String message) {
		super(message);
	}
}
