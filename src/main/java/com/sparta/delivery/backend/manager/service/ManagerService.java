package com.sparta.delivery.backend.manager.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.global.excpetion.DuplicateUsernameException;
import com.sparta.delivery.backend.manager.dto.ReqCreateManagerDto;
import com.sparta.delivery.backend.manager.entity.Manager;
import com.sparta.delivery.backend.manager.repository.ManagerRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;
import com.sparta.delivery.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {
	private final ManagerRepository managerRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void createManager(ReqCreateManagerDto requestDto) {
		//같은 메일로 다른 사용자 요청 가능하도록 할지 정책 결정필요
		userRepository.findByUsernameAndDeletedAtIsNull(requestDto.getUsername())
			.ifPresent(user -> {
				throw new DuplicateUsernameException("이미 존재하는 사용자명입니다.");
			});

		User user = User.builder()
			.username(requestDto.getUsername())
			.password(passwordEncoder.encode(requestDto.getPassword()))
			.role(UserRoleEnum.MANAGER)
			.build();
		Manager manager = Manager.builder()
			.user(user)
			.name(requestDto.getName())
			.phoneNumber(requestDto.getPhoneNumber())
			.email(requestDto.getEmail())
			.build();

		managerRepository.save(manager);
	}
}
