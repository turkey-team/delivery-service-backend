package com.sparta.delivery.backend.owner.entity;

import com.sparta.delivery.backend.global.common.BaseEntity;
import com.sparta.delivery.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_owner")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "p_user_id", nullable = false)
    private User user;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "email", length = 512, unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Builder
    private Owner(User user, String nickname, String email, String phoneNumber) {
        this.user = user;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public void updateNickname(String nickname) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
    }

	public void delete(Long deletedBy) {
		this.email = this.email + "_deleted_" + getId();
		this.softDelete(deletedBy);
		user.softDelete(deletedBy); // 다른 PR(고객 탈퇴 기능)에 포함된 기능 추후 반영
	}
}
