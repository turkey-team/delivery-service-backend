package com.sparta.delivery.backend.customer.entity;

import com.sparta.delivery.backend.global.common.BaseEntity;
import com.sparta.delivery.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_customer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "p_user_id", nullable = false)
    private User user;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "email", length = 512, unique = true)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Builder
    private Customer(User user, String nickname, String email, String phoneNumber) {
        this.user = user;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

	public String getUsername() {
		return this.user.getUsername();
	}

	public UUID getUserPublicId() {
		return this.user.getPublicId();
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void delete(Long userId) {
		this.email = email + "_deleted_" + getId();
		this.softDelete(userId);
		user.softDelete(userId);
	}
}
