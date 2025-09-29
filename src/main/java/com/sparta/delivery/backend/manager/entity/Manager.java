package com.sparta.delivery.backend.manager.entity;

import com.sparta.delivery.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_manager")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Manager {

    @Id
    @UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_user_id", nullable = false)
    private User user;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 320, unique = true)
    private String email;

    @Builder
    private Manager(User user, String username, String phoneNumber, String email) {
        this.user = user;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
