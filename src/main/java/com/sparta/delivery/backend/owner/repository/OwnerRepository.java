package com.sparta.delivery.backend.owner.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.owner.entity.Owner;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, UUID> {

	Optional<Owner> findByUserIdAndDeletedAtIsNull(Long id);
	Optional<Owner> findByUserId(Long id);

	Optional<Owner> findByIdAndDeletedAtIsNull(UUID ownerId);
}
