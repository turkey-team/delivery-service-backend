package com.sparta.delivery.backend.global.excpetion;

public class DuplicateUsernameException extends RuntimeException {
	public DuplicateUsernameException(String message) {
		super(message);
	}
}
