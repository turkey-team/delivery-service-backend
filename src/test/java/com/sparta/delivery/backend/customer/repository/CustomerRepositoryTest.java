package com.sparta.delivery.backend.customer.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@DataJpaTest
public class CustomerRepositoryTest {
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private TestEntityManager entityManager;

	private User user;
	private Customer customer;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.username("testuser")
			.password("password123")
			.role(UserRoleEnum.CUSTOMER)
			.build();
		entityManager.persist(user);

		customer = Customer.builder()
			.user(user)
			.nickname("테스트닉네임")
			.email("test@example.com")
			.phoneNumber("010-1234-5678")
			.build();
		entityManager.persist(customer);
		entityManager.flush();
	}

	@Nested
	@DisplayName("삭제되지 않은 고객 조회 테스트")
	class FindByUserIdAndDeletedAtIsNullTest {

		@Test
		@DisplayName("성공 - 삭제되지 않은 고객 조회")
		void findActiveCustomer_Success() {
			// when
			Optional<Customer> result = customerRepository.findByUserIdAndDeletedAtIsNull(user.getId());

			// then
			assertTrue(result.isPresent());
			assertEquals(customer.getNickname(), result.get().getNickname());
			assertEquals(customer.getEmail(), result.get().getEmail());
			assertNull(result.get().getDeletedAt());
		}

		@Test
		@DisplayName("실패 - 삭제된 고객은 조회 안됨")
		void findDeletedCustomer_NotFound() {
			// given
			customer.softDelete(1L);
			entityManager.persist(customer);
			entityManager.flush();

			// when
			Optional<Customer> result = customerRepository.findByUserIdAndDeletedAtIsNull(user.getId());

			// then
			assertFalse(result.isPresent());
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 유저 ID")
		void findByNonExistentUserId_NotFound() {
			// when
			Optional<Customer> result = customerRepository.findByUserIdAndDeletedAtIsNull(999L);

			// then
			assertFalse(result.isPresent());
		}
	}
}
