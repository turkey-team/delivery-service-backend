package com.sparta.delivery.backend.global.infra.email;

public interface EmailSender {
	/**
	 * 단순 텍스트 메일 발송
	 */
	void sendMail(String to, String subject, String text);
}
