package com.sparta.delivery.backend.customer.entity;

import com.sparta.delivery.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_customer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
	@UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_user_id", nullable = false)
    private User user;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "email", length = 320, unique = true)
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

    public void update(String nickname, String email, String phoneNumber) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (email != null) {
            this.email = email;
        }
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
    }
}
