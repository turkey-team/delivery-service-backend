package com.sparta.delivery.backend.manager.entity;

import java.util.UUID;

import com.sparta.delivery.backend.global.common.BaseEntity;
import com.sparta.delivery.backend.global.common.util.PhoneNumberFormatter;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_manager")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Manager extends BaseEntity {

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "p_user_id", nullable = false)
	private User user;

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "phone_number", length = 20)
	private String phoneNumber;

	@Column(name = "email", length = 320)
	private String email;

	@Builder
	private Manager(User user, String name, String phoneNumber, String email) {
		this.user = user;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public String getUsername() {
		return this.user.getUsername();
	}

	public UUID getUserPublicId() {
		return this.user.getPublicId();
	}

	public UserRoleEnum getUserRole() {
		return this.user.getRole();
	}

	public void updateRole(UserRoleEnum role) {
		this.user.updateRole(role);
	}

	public void delete(Long userId) {
		this.email = email + "_deleted_" + getId();
		this.softDelete(userId);
		user.softDelete(userId);
	}

	public String getFormattedPhoneNumber() {
		return PhoneNumberFormatter.formatPhoneNumber(phoneNumber);
	}

}
