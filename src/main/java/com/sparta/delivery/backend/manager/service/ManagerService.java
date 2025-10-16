package com.sparta.delivery.backend.manager.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.global.excpetion.DuplicateUsernameException;
import com.sparta.delivery.backend.manager.dto.ReqCreateManagerDto;
import com.sparta.delivery.backend.manager.dto.ReqUpdateManagerDto;
import com.sparta.delivery.backend.manager.dto.ResGetManagerDetailDto;
import com.sparta.delivery.backend.manager.dto.ResGetManagerSummaryDto;
import com.sparta.delivery.backend.manager.entity.Manager;
import com.sparta.delivery.backend.manager.repository.ManagerRepository;
import com.sparta.delivery.backend.security.UserDetailsImpl;
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

	@Transactional(readOnly = true)
	public List<ResGetManagerSummaryDto> getAllManagers() {
		return managerRepository.findAllByDeletedAtIsNull().stream()
			.map(ResGetManagerSummaryDto::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public ResGetManagerDetailDto getManager(UUID managerUserPublicId) {
		Manager manager = managerRepository.findByUserPublicIdAndDeletedAtIsNull(managerUserPublicId)
			.orElseThrow(() -> new IllegalArgumentException("잘못된 유저 아이디입니다."));

		return ResGetManagerDetailDto.from(manager);
	}

	@Transactional
	public void updateManager(UUID managerUserPublicId, ReqUpdateManagerDto requestDto) {
		Manager manager = managerRepository.findByUserPublicIdAndDeletedAtIsNull(managerUserPublicId)
			.orElseThrow(() -> new IllegalArgumentException("잘못된 유저 아이디입니다."));

		manager.updateManager(requestDto.getEmail(), requestDto.getName(), requestDto.getPhoneNumber());
	}

	@Transactional
	public void deleteManager(UUID managerUserPublicId, UserDetailsImpl userDetails) {
		Manager manager = managerRepository.findByUserPublicIdAndDeletedAtIsNull(managerUserPublicId)
			.orElseThrow(() -> new IllegalArgumentException("잘못된 유저 아이디입니다."));

		if (manager.getUserRole().equals(UserRoleEnum.MASTER)) {
			throw new IllegalArgumentException("MASTER는 삭제할 수 없습니다.");
		}

		manager.delete(userDetails.getId());
	}

}
