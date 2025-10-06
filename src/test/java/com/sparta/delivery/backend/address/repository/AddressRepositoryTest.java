package com.sparta.delivery.backend.address.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("AddressRepository 테스트")
class AddressRepositoryTest {

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private TestEntityManager entityManager;

	private User testUser;
	private User otherUser;
	private Dong testDong;
	private Dong otherDong;

	@BeforeEach
	void setUp() {
		// 테스트 사용자 생성
		testUser = User.builder()
			.username("testUser")
			.password("password123")
			.role(UserRoleEnum.CUSTOMER)
			.build();
		entityManager.persist(testUser);

		otherUser = User.builder()
			.username("otherUser")
			.password("password456")
			.role(UserRoleEnum.CUSTOMER)
			.build();
		entityManager.persist(otherUser);

		Sido seoul = createSido("서울특별시", "11");
		entityManager.persist(seoul);

		Sigungu gangnam = createSigungu(seoul, "강남구", "680");
		entityManager.persist(gangnam);

		Sigungu seocho = createSigungu(seoul, "서초구", "650");
		entityManager.persist(seocho);

		testDong = createDong(gangnam,"102","역삼동");
		entityManager.persist(testDong);

		otherDong = createDong(seocho,"103","서초동");
		entityManager.persist(otherDong);

		entityManager.flush();
		entityManager.clear();
	}

	@Nested
	@DisplayName("findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc 메서드는")
	class FindAllByUserIdTest {

		@Test
		@DisplayName("삭제되지 않은 주소들을 생성일 역순으로 조회한다")
		void success_findAllActiveAddressesOrderByCreatedAtDesc() {
			// given
			Address address1 = createAddress(testUser, testDong, "강남구 테헤란로 1");
			entityManager.persist(address1);
			entityManager.flush();

			sleep();

			Address address2 = createAddress(testUser, testDong, "강남구 테헤란로 2");
			entityManager.persist(address2);
			entityManager.flush();

			sleep();

			Address address3 = createAddress(testUser, testDong, "강남구 테헤란로 3");
			entityManager.persist(address3);
			entityManager.flush();
			entityManager.clear();

			// when
			List<Address> addresses = addressRepository
				.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(testUser.getId());

			// then
			assertThat(addresses).hasSize(3);
			assertThat(addresses.get(0).getAddress()).isEqualTo("강남구 테헤란로 3");
			assertThat(addresses.get(1).getAddress()).isEqualTo("강남구 테헤란로 2");
			assertThat(addresses.get(2).getAddress()).isEqualTo("강남구 테헤란로 1");

			// 생성 시간 순서 확인
			assertThat(addresses.get(0).getCreatedAt())
				.isAfterOrEqualTo(addresses.get(1).getCreatedAt());
			assertThat(addresses.get(1).getCreatedAt())
				.isAfterOrEqualTo(addresses.get(2).getCreatedAt());
		}

		@Test
		@DisplayName("삭제된 주소는 조회 결과에서 제외된다")
		void success_excludeDeletedAddresses() {
			// given
			Address activeAddress = createAddress(testUser, testDong, "활성 주소");
			entityManager.persist(activeAddress);

			Address deletedAddress = createAddress(testUser, testDong, "삭제된 주소");
			entityManager.persist(deletedAddress);
			entityManager.flush();

			// 주소 소프트 삭제
			deletedAddress.softDelete(testUser.getId());
			entityManager.flush();
			entityManager.clear();

			// when
			List<Address> addresses = addressRepository
				.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(testUser.getId());

			// then
			assertThat(addresses).hasSize(1);
			assertThat(addresses.get(0).getAddress()).isEqualTo("활성 주소");
			assertThat(addresses.get(0).isDeleted()).isFalse();
		}

		@Test
		@DisplayName("다른 사용자의 주소는 조회되지 않는다")
		void success_excludeOtherUsersAddresses() {
			// given
			Address myAddress1 = createAddress(testUser, testDong, "내 주소 1");
			Address myAddress2 = createAddress(testUser, testDong, "내 주소 2");
			Address othersAddress = createAddress(otherUser, otherDong, "다른 사람 주소");

			entityManager.persist(myAddress1);
			entityManager.persist(myAddress2);
			entityManager.persist(othersAddress);
			entityManager.flush();
			entityManager.clear();

			// when
			List<Address> addresses = addressRepository
				.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(testUser.getId());

			// then
			assertThat(addresses).hasSize(2);
			assertThat(addresses)
				.extracting(Address::getAddress)
				.containsExactlyInAnyOrder("내 주소 1", "내 주소 2")
				.doesNotContain("다른 사람 주소");
		}

		@Test
		@DisplayName("주소가 하나도 없으면 빈 리스트를 반환한다")
		void success_returnEmptyListWhenNoAddresses() {
			// when
			List<Address> addresses = addressRepository
				.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(testUser.getId());

			// then
			assertThat(addresses).isEmpty();
		}

		@Test
		@DisplayName("모든 주소가 삭제된 경우 빈 리스트를 반환한다")
		void success_returnEmptyListWhenAllAddressesDeleted() {
			// given
			Address address1 = createAddress(testUser, testDong, "주소 1");
			Address address2 = createAddress(testUser, testDong, "주소 2");
			entityManager.persist(address1);
			entityManager.persist(address2);
			entityManager.flush();

			address1.softDelete(testUser.getId());
			address2.softDelete(testUser.getId());
			entityManager.flush();
			entityManager.clear();

			// when
			List<Address> addresses = addressRepository
				.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(testUser.getId());

			// then
			assertThat(addresses).isEmpty();
		}
	}

	@Nested
	@DisplayName("findByIdAndDeletedAtIsNull 메서드는")
	class FindByIdTest {

		@Test
		@DisplayName("삭제되지 않은 주소를 ID로 조회한다")
		void success_findActiveAddressById() {
			// given
			Address address = createAddress(testUser, testDong, "서울시 강남구 역삼동 123");
			entityManager.persist(address);
			entityManager.flush();
			entityManager.clear();

			UUID addressId = address.getId();

			// when
			Optional<Address> foundAddress = addressRepository
				.findByIdAndDeletedAtIsNull(addressId);

			// then
			assertThat(foundAddress).isPresent();
			assertThat(foundAddress.get().getId()).isEqualTo(addressId);
			assertThat(foundAddress.get().getAddress()).isEqualTo("서울시 강남구 역삼동 123");
			assertThat(foundAddress.get().isDeleted()).isFalse();
			assertThat(foundAddress.get().getDeletedAt()).isNull();
			assertThat(foundAddress.get().getUser().getId()).isEqualTo(testUser.getId());
			assertThat(foundAddress.get().getDong().getId()).isEqualTo(testDong.getId());
		}

		@Test
		@DisplayName("삭제된 주소는 조회되지 않는다")
		void fail_notFindDeletedAddress() {
			// given
			Address address = createAddress(testUser, testDong, "삭제될 주소");
			entityManager.persist(address);
			entityManager.flush();

			UUID addressId = address.getId();
			address.softDelete(testUser.getId());
			entityManager.flush();
			entityManager.clear();

			// when
			Optional<Address> foundAddress = addressRepository
				.findByIdAndDeletedAtIsNull(addressId);

			// then
			assertThat(foundAddress).isEmpty();
		}

		@Test
		@DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
		void fail_returnEmptyForNonExistentId() {
			// given
			UUID nonExistentId = UUID.randomUUID();

			// when
			Optional<Address> foundAddress = addressRepository
				.findByIdAndDeletedAtIsNull(nonExistentId);

			// then
			assertThat(foundAddress).isEmpty();
		}

		@Test
		@DisplayName("null ID로 조회하면 빈 Optional을 반환한다")
		void fail_returnEmptyForNullId() {
			// when
			Optional<Address> foundAddress = addressRepository
				.findByIdAndDeletedAtIsNull(null);

			// then
			assertThat(foundAddress).isEmpty();
		}
	}

	private Address createAddress(User user, Dong dong, String address) {
		return Address.builder()
			.user(user)
			.dong(dong)
			.address(address)
			.build();
	}

	private Sido createSido(String name, String code) {
		return Sido.builder()
			.name(name)
			.code(code)
			.build();
	}

	private Sigungu createSigungu(Sido sido, String name, String code) {
		return Sigungu.builder()
			.sido(sido)
			.name(name)
			.code(code)
			.build();
	}

	private Dong createDong(Sigungu sigungu, String code, String name) {
		return Dong.builder()
			.sigungu(sigungu)
			.code(code)
			.name(name)
			.build();
	}

	private void sleep() {
		try {
			Thread.sleep((long)10);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}