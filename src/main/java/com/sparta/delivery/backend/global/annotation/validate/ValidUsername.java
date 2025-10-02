package com.sparta.delivery.backend.global.annotation.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotBlank(message = "아이디는 필수입니다.")
@Pattern(regexp = "^[a-z0-9]{4,10}$", message = "아이디는 4~10자의 소문자 알파벳과 숫자로만 구성되어야 합니다.")
public @interface ValidUsername {
	String message() default "아이디는 4~10자의 소문자 알파벳과 숫자로만 구성되어야 합니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
