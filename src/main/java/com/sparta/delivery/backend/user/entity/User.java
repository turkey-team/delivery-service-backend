package com.sparta.delivery.backend.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

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
    @Column(name = "role", length = 20, nullable = false)
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
		this.username = username + "_deleted_" + this.id;
		this.deletedAt = Instant.now();
		this.deletedBy = userId;
	}
}
