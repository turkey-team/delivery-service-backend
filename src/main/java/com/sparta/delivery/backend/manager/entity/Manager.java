package com.sparta.delivery.backend.manager.entity;

import com.sparta.delivery.backend.global.common.BaseEntity;
import com.sparta.delivery.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

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

}
