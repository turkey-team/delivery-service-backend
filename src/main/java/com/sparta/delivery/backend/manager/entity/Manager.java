package com.sparta.delivery.backend.manager.entity;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_manager")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Manager extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "p_user_id", nullable = false)
    private User user;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 320, unique = true)
    private String email;

	@ManyToOne
	@JoinColumn(name = "p_store_id")
	private Store store;

    @Builder
    private Manager(User user, String name, String phoneNumber, String email) {
        this.user = user;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

	public void setStore(Store store) {
		if (this.store != null) {
			//기존근무가게가있을경우 삭제
			this.store.getManagers().remove(this);
		}
		//근무지 저장
		this.store = store;
		//가게에 매니저 추가
		store.getManagers().add(this);
	}
}
