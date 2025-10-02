package com.sparta.delivery.backend.owner.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.global.excpetion.DuplicateUsernameException;
import com.sparta.delivery.backend.owner.dto.ReqCreateOwnerDto;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.owner.repository.OwnerRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;
import com.sparta.delivery.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerService {
	private final OwnerRepository ownerRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	//TODO: 1. 사업자 등록번호 검증, 2. 이메일 인증
	@Transactional
	public void createOwner(ReqCreateOwnerDto requestDto) {
		//같은 메일로 다른 사용자 요청 가능하도록 할지 정책 결정필요
		userRepository.findByUsername(requestDto.getUsername())
			.ifPresent(user -> {
				throw new DuplicateUsernameException("이미 존재하는 사용자명입니다.");
			});

		User user = User.builder()
			.username(requestDto.getUsername())
			.password(passwordEncoder.encode(requestDto.getPassword()))
			.role(UserRoleEnum.CUSTOMER)
			.build();
		Owner owner = Owner.builder()
			.user(user)
			.nickname(requestDto.getNickname())
			.phoneNumber(requestDto.getPhoneNumber())
			.email(requestDto.getEmail())
			.businessNumber(requestDto.getBusinessNumber())
			.build();

		ownerRepository.save(owner);
	}
}
