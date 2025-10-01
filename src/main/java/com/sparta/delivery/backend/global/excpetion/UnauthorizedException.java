package com.sparta.delivery.backend.global.excpetion;

public class UnauthorizedException extends RuntimeException {
	public UnauthorizedException(String message) {
		super(message);
	}
}
