package com.sparta.delivery.backend.customer.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.entity.CustomerAddress;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, UUID> {

	@Query("SELECT ca FROM CustomerAddress ca " +
		"JOIN FETCH ca.address a " +
		"JOIN FETCH a.dong d " +
		"WHERE ca.customer = :customer " +
		"AND ca.deletedAt IS NULL " +
		"ORDER BY ca.isDefault DESC, ca.createdAt DESC")
	List<CustomerAddress> findAllByCustomerAndDeletedAtIsNullOrderByIsDefaultDescCreatedAtDesc(
		@Param("customer") Customer customer);

	@Query("SELECT ca FROM CustomerAddress ca " +
		"JOIN FETCH ca.address a " +
		"JOIN FETCH a.dong " +
		"WHERE ca.customer = :customer " +
		"AND ca.isDefault = true " +
		"AND ca.deletedAt IS NULL")
	Optional<CustomerAddress> findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(@Param("customer") Customer customer);

	@Query("SELECT ca FROM CustomerAddress ca " +
		"JOIN FETCH ca.address a " +
		"JOIN FETCH a.dong " +
		"WHERE ca.id = :id " +
		"AND ca.deletedAt IS NULL")
	Optional<CustomerAddress> findByIdAndDeletedAtIsNull(@Param("id") UUID id);

	boolean existsByAddressAndDeletedAtIsNull(Address address);

	long countByCustomerAndDeletedAtIsNull(Customer customer);
}
