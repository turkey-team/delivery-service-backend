package com.sparta.delivery.backend.user.repository;

import java.util.Optional;

import com.sparta.delivery.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsernameAndDeletedAtIsNull(String username);

	Optional<User> findByIdAndDeletedAtIsNull(Long id);
}
