package com.sparta.delivery.backend.user.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "public_id", nullable = false, unique = true)
	private UUID publicId;

	@Column(name = "username", length = 100, nullable = false, unique = true)
	private String username;

	@Column(name = "password", length = 255, nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "role", nullable = false)
	private UserRoleEnum role;

	private Instant deletedAt;

	private Long deletedBy;

	@Builder
	private User(String username, String password, UserRoleEnum role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}

	@PrePersist
	public void generatePublicId() {
		if (this.publicId == null) {
			this.publicId = UUID.randomUUID();
		}
	}

	public void changePassword(String encodedPassword) {
		password = encodedPassword;
	}

	public void softDelete(Long userId) {
		if (this.deletedAt == null) {
			this.username = username + "_deleted_" + this.id;
			this.deletedAt = Instant.now();
			this.deletedBy = userId;
		}
	}
}
