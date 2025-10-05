package com.sparta.delivery.backend.global.infra.email;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JavaEmailSenderImpl implements EmailSender{
	private final JavaMailSender javaMailSender;

	@Override
	public void sendMail(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);

			javaMailSender.send(message);
			log.info("메일 발송 성공: to={}", to);

		} catch (MailException e) {
			log.error("메일 발송 실패: to={}", to, e);
			throw new RuntimeException("메일 발송에 실패했습니다.", e);
		}
	}
}
