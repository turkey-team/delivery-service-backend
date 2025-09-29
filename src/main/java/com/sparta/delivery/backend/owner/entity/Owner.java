package com.sparta.delivery.backend.owner.entity;

import com.sparta.delivery.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_owner")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner {

    @Id
    @UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_user_id", nullable = false)
    private User user;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "email", length = 320, unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "business_number", length = 12)
    private String businessNumber;

    @Builder
    private Owner(User user, String nickname, String email, String phoneNumber, String businessNumber) {
        this.user = user;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.businessNumber = businessNumber;
    }
}
