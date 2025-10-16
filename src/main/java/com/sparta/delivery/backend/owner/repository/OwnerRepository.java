package com.sparta.delivery.backend.owner.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.owner.entity.Owner;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, UUID> {

	Optional<Owner> findByIdAndDeletedAtIsNull(UUID ownerId);
	
	Optional<Owner> findByUserId(Long id);
	
	@Query("SELECT o FROM Owner o " +
		"JOIN FETCH o.user " +
		"WHERE o.user.id = :userId " +
		"AND o.deletedAt IS NULL")
	Optional<Owner> findByUserIdAndDeletedAtIsNull(@Param("userId") Long userId);
	
	@Query("SELECT o FROM Owner o " +
		"JOIN FETCH o.user " +
		"WHERE o.user.publicId = :publicId " +
		"AND o.deletedAt IS NULL")
	Optional<Owner> findByUserPublicIdAndDeletedAtIsNull(@Param("publicId") UUID userPublicId);
	
	@Query("SELECT o FROM Owner o " +
		"JOIN FETCH o.user " +
		"WHERE o.email = :email " +
		"AND o.deletedAt IS NULL")
	Optional<Owner> findByEmailAndDeletedAtIsNull(@Param("email") String email);

	Page<Owner> findByNicknameContainingAndDeletedAtIsNull(String nickname, Pageable pageable);

	Page<Owner> findAllByDeletedAtIsNull(Pageable pageable);
}
