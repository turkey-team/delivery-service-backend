package com.sparta.delivery.backend.customer.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.customer.dto.ReqChangePasswordDto;
import com.sparta.delivery.backend.customer.dto.ReqCreateCustomerDto;
import com.sparta.delivery.backend.customer.dto.ReqPasswordResetDto;
import com.sparta.delivery.backend.customer.dto.ReqPasswordResetRequestDto;
import com.sparta.delivery.backend.customer.dto.ReqUpdateCustomerDto;
import com.sparta.delivery.backend.customer.dto.ResGetCustomerDto;
import com.sparta.delivery.backend.customer.dto.ResGetMyCustomerDto;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.global.excpetion.DuplicateUsernameException;
import com.sparta.delivery.backend.global.infra.email.EmailSender;
import com.sparta.delivery.backend.global.infra.redis.RedisKeyConstants;
import com.sparta.delivery.backend.global.verification.EmailVerificationTokenValidator;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;
import com.sparta.delivery.backend.user.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerService {
	private final CustomerRepository customerRepository;
	private final RedisTemplate redisTemplate;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailVerificationTokenValidator emailVerificationTokenValidator;
	private final EmailSender emailSender;

	@Transactional
	public void createCustomer(ReqCreateCustomerDto requestDto) {
		//같은 메일로 다른 사용자 요청 가능하도록 할지 정책 결정필요
		userRepository.findByUsernameAndDeletedAtIsNull(requestDto.getUsername())
			.ifPresent(user -> {
				throw new DuplicateUsernameException("이미 존재하는 사용자명입니다.");
			});

		emailVerificationTokenValidator.validateAndConsumeToken(requestDto.getEmail(),
			requestDto.getEmailVerificationToken());

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
		Customer customer = getCustomerByUserId(userDetails);

		return ResGetMyCustomerDto.from(customer);
	}

	public ResGetCustomerDto getCustomerByUserPublicId(UUID customerUserPublicId) {
		Customer customer = customerRepository.findByUserPublicIdAndDeletedAtNull(customerUserPublicId)
			.orElseThrow(() -> new IllegalArgumentException("잘못된 유저 아이디 입니다."));

		return ResGetCustomerDto.from(customer);
	}

	@Transactional
	public void updateCurrentCustomer(UserDetailsImpl userDetails, ReqUpdateCustomerDto requestDto) {
		Customer customer = getCustomerByUserId(userDetails);
		customer.updateNickname(requestDto.getNickname());
	}

	@Transactional
	public void changePassword(UserDetailsImpl userDetails, @Valid ReqChangePasswordDto requestDto) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userDetails.getId())
			.orElseThrow(() -> new IllegalArgumentException("잘못된 유저 아이디 입니다."));

		if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
			throw new IllegalArgumentException("잘못된 비밀번호입니다.");
		}
		if (!requestDto.getNewPassword().equals(requestDto.getNewPasswordConfirm())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		String newEncodedPassword = passwordEncoder.encode(requestDto.getNewPassword());
		user.changePassword(newEncodedPassword);
	}

	@Transactional
	public void requestPasswordReset(ReqPasswordResetRequestDto requestDto) {
		Optional<Customer> customerOpt = customerRepository.findByEmailAndDeletedAtIsNull(requestDto.getEmail());

		if (customerOpt.isEmpty()) {
			log.info("비밀번호 재설정 요청 - 존재하지 않는 이메일: {}", requestDto.getEmail());
			return;
		}

		Customer customer = customerOpt.get();

		// 토큰 생성 및 이메일 발송
		String resetToken = UUID.randomUUID().toString();
		String key = RedisKeyConstants.PASSWORD_RESET_PREFIX + customer.getEmail();
		redisTemplate.opsForValue().set(key, resetToken, 30, TimeUnit.MINUTES);

		//TODO: 프론트 존재시 프론트의 링크에 parameter를 포함시켜 해당 링크로 이동하도록 구성
		String emailBody = String.format("""
        비밀번호 재설정 요청이 접수되었습니다.
        
        아래 정보를 사용하여 비밀번호를 재설정하세요:
        
        이메일: %s
        토큰: %s
        
        [Swagger 또는 Postman에서 테스트]
        POST /v1/customers/password-reset/confirm
        
        요청 본문:
        {
          "email": "%s",
          "token": "%s",
          "newPassword": "새비밀번호",
          "newPasswordConfirm": "새비밀번호"
        }
        
        이 토큰은 30분간 유효합니다.
        """,
			customer.getEmail(),
			resetToken,
			customer.getEmail(),
			resetToken
		);
		emailSender.sendMail(customer.getEmail(), "칠면조 배달서비스 비밀번호 재설정 메일입니다.", emailBody);
	}

	@Transactional
	public void resetPassword(ReqPasswordResetDto requestDto) {
		String key = RedisKeyConstants.PASSWORD_RESET_PREFIX + requestDto.getEmail();
		Object storedToken = redisTemplate.opsForValue().get(key);

		if (storedToken == null || !storedToken.toString().equals(requestDto.getToken())) {
			throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
		}

		if (!requestDto.getNewPassword().equals(requestDto.getNewPasswordConfirm())) {
			throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
		}

		Customer customer = customerRepository.findByEmailAndDeletedAtIsNull(requestDto.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		User user = customer.getUser();
		user.changePassword(passwordEncoder.encode(requestDto.getNewPassword()));

		redisTemplate.delete(key);
	}

	@Transactional
	public void deleteCurrentCustomer(UserDetailsImpl userDetails) {
		Customer customer = getCustomerByUserId(userDetails);
		customer.delete(userDetails.getId());
	}

	@Transactional
	public void deleteCustomerByManager(UserDetailsImpl userDetails, UUID customerUserPublicId) {
		Customer customer = customerRepository.findByUserPublicIdAndDeletedAtNull(customerUserPublicId)
			.orElseThrow(() -> new IllegalArgumentException("잘못된 유저 아이디 입니다."));

		customer.delete(userDetails.getId());
	}

	private Customer getCustomerByUserId(UserDetailsImpl userDetails) {
		Customer customer = customerRepository.findByUserIdAndDeletedAtIsNull(userDetails.getId())
			.orElseThrow(() -> new IllegalArgumentException("잘못된 유저 아이디 입니다."));

		return customer;
	}

}
