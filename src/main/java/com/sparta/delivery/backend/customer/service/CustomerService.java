package com.sparta.delivery.backend.customer.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.customer.dto.ReqCreateCustomerDto;
import com.sparta.delivery.backend.customer.dto.ResGetCustomerDto;
import com.sparta.delivery.backend.customer.dto.ResGetMyCustomerDto;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.global.excpetion.DuplicateUsernameException;
import com.sparta.delivery.backend.global.verification.EmailVerificationTokenValidator;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;
import com.sparta.delivery.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {
	private final CustomerRepository customerRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailVerificationTokenValidator emailVerificationTokenValidator;

	@Transactional
	public void createCustomer(ReqCreateCustomerDto requestDto) {
		//같은 메일로 다른 사용자 요청 가능하도록 할지 정책 결정필요
		userRepository.findByUsername(requestDto.getUsername())
			.ifPresent(user -> {
				throw new DuplicateUsernameException("이미 존재하는 사용자명입니다.");
			});

		emailVerificationTokenValidator.validateAndConsumeToken(requestDto.getEmail(), requestDto.getEmailVerificationToken());

		User user = User.builder()
			.username(requestDto.getUsername())
			.password(passwordEncoder.encode(requestDto.getPassword()))
			.role(UserRoleEnum.CUSTOMER)
			.build();
		Customer customer = Customer.builder()
			.user(user)
			.email(requestDto.getEmail())
			.nickname(requestDto.getNickname())
			.phoneNumber(requestDto.getPhoneNumber())
			.build();
		customerRepository.save(customer);
	}

	public ResGetMyCustomerDto getCurrentCustomer(UserDetailsImpl userDetails) {
		Customer customer = customerRepository.findByUserIdAndDeletedAtIsNull(userDetails.getId())
			.orElseThrow(() -> new IllegalArgumentException("잘못된 유저 아이디 입니다."));

		return ResGetMyCustomerDto.from(customer);
	}

	public ResGetCustomerDto getCustomerByUserPublicId(UUID customerUserPublicId) {
		Customer customer = customerRepository.findByUserPublicIdAndDeletedAtNull(customerUserPublicId)
			.orElseThrow(() -> new IllegalArgumentException("잘못된 유저 아이디 입니다."));

		return ResGetCustomerDto.from(customer);
	}
}
