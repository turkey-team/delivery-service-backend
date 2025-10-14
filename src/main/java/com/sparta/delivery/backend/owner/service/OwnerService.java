package com.sparta.delivery.backend.owner.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.global.excpetion.DuplicateUsernameException;
import com.sparta.delivery.backend.global.infra.email.EmailSender;
import com.sparta.delivery.backend.global.infra.redis.RedisKeyConstants;
import com.sparta.delivery.backend.global.verification.EmailVerificationTokenValidator;
import com.sparta.delivery.backend.owner.dto.ReqChangePasswordDto;
import com.sparta.delivery.backend.owner.dto.ReqCreateOwnerDto;
import com.sparta.delivery.backend.owner.dto.ReqPasswordResetDto;
import com.sparta.delivery.backend.owner.dto.ReqPasswordResetRequestDto;
import com.sparta.delivery.backend.owner.dto.ReqUpdateOwnerDto;
import com.sparta.delivery.backend.owner.dto.ResGetMyOwnerDto;
import com.sparta.delivery.backend.owner.dto.ResGetOwnerDto;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.owner.repository.OwnerRepository;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;
import com.sparta.delivery.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OwnerService {
	private final OwnerRepository ownerRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailVerificationTokenValidator emailVerificationTokenValidator;
	private final RedisTemplate redisTemplate;
	private final EmailSender emailSender;

	@Transactional
	public void createOwner(ReqCreateOwnerDto requestDto) {
		userRepository.findByUsernameAndDeletedAtIsNull(requestDto.getUsername())
			.ifPresent(user -> {
				throw new DuplicateUsernameException("이미 존재하는 사용자명입니다.");
			});
		emailVerificationTokenValidator.validateAndConsumeToken(requestDto.getEmail(), requestDto.getEmailVerificationToken());

		User user = User.builder()
			.username(requestDto.getUsername())
			.password(passwordEncoder.encode(requestDto.getPassword()))
			.role(UserRoleEnum.OWNER)
			.build();
		Owner owner = Owner.builder()
			.user(user)
			.nickname(requestDto.getNickname())
			.phoneNumber(requestDto.getPhoneNumber())
			.email(requestDto.getEmail())
			.build();

		ownerRepository.save(owner);
	}

	public ResGetMyOwnerDto getCurrentOwner(UserDetailsImpl userDetails) {
		Owner owner = getOwnerByUserId(userDetails);
		return ResGetMyOwnerDto.from(owner);
	}

	public ResGetOwnerDto getOwnerByUserPublicId(UUID ownerUserPublicId) {
		Owner owner = ownerRepository.findByUserPublicIdAndDeletedAtIsNull(ownerUserPublicId)
			.orElseThrow(() -> new IllegalArgumentException("잘못된 유저 아이디 입니다."));
		return ResGetOwnerDto.from(owner);
	}

	@Transactional
	public void updateCurrentOwner(UserDetailsImpl userDetails, ReqUpdateOwnerDto requestDto) {
		Owner owner = getOwnerByUserId(userDetails);
		owner.updateNickname(requestDto.getNickname());
	}

	@Transactional
	public void changePassword(UserDetailsImpl userDetails, ReqChangePasswordDto requestDto) {
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
		Optional<Owner> ownerOpt = ownerRepository.findByEmailAndDeletedAtIsNull(requestDto.getEmail());

		if (ownerOpt.isEmpty()) {
			log.info("비밀번호 재설정 요청 - 존재하지 않는 이메일: {}", requestDto.getEmail());
			return;
		}

		Owner owner = ownerOpt.get();

		// 토큰 생성 및 이메일 발송
		String resetToken = UUID.randomUUID().toString();
		String key = RedisKeyConstants.PASSWORD_RESET_PREFIX + owner.getEmail();
		redisTemplate.opsForValue().set(key, resetToken, 30, TimeUnit.MINUTES);

		//TODO: 프론트 존재시 프론트의 링크에 parameter를 포함시켜 해당 링크로 이동하도록 구성
		String emailBody = String.format("""
        비밀번호 재설정 요청이 접수되었습니다.
        
        아래 정보를 사용하여 비밀번호를 재설정하세요:
        
        이메일: %s
        토큰: %s
        
        [Swagger 또는 Postman에서 테스트]
        POST /v1/owners/password-reset/confirm
        
        요청 본문:
        {
          "email": "%s",
          "token": "%s",
          "newPassword": "새비밀번호",
          "newPasswordConfirm": "새비밀번호"
        }
        
        이 토큰은 30분간 유효합니다.
        """,
			owner.getEmail(),
			resetToken,
			owner.getEmail(),
			resetToken
		);
		emailSender.sendMail(owner.getEmail(), "칠면조 배달서비스 비밀번호 재설정 메일입니다.", emailBody);
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

		Owner owner = ownerRepository.findByEmailAndDeletedAtIsNull(requestDto.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		User user = owner.getUser();
		user.changePassword(passwordEncoder.encode(requestDto.getNewPassword()));

		redisTemplate.delete(key);
	}

	@Transactional
	public void deleteCurrentOwner(UserDetailsImpl userDetails) {
		Owner owner = getOwnerByUserId(userDetails);
		owner.delete(userDetails.getId());
	}

	@Transactional
	public void deleteOwnerByManager(UserDetailsImpl userDetails, UUID ownerUserPublicId) {
		Owner owner = ownerRepository.findByUserPublicIdAndDeletedAtIsNull(ownerUserPublicId)
			.orElseThrow(() -> new IllegalArgumentException("잘못된 유저 아이디 입니다."));
		owner.delete(userDetails.getId());
	}

	private Owner getOwnerByUserId(UserDetailsImpl userDetails) {
		return ownerRepository.findByUserIdAndDeletedAtIsNull(userDetails.getId())
			.orElseThrow(() -> new IllegalArgumentException("잘못된 유저 아이디 입니다."));
	}
}
