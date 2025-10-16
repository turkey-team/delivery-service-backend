// package com.sparta.delivery.backend.address.repository;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
// import org.springframework.test.context.ActiveProfiles;
//
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;
//
// import static org.assertj.core.api.Assertions.assertThat;
//
// import com.sparta.delivery.backend.address.entity.Address;
// import com.sparta.delivery.backend.region.entity.Dong;
// import com.sparta.delivery.backend.region.entity.Sido;
// import com.sparta.delivery.backend.region.entity.Sigungu;
// import com.sparta.delivery.backend.user.entity.User;
// import com.sparta.delivery.backend.user.entity.UserRoleEnum;
//
// @DataJpaTest
// @ActiveProfiles("test")
// @DisplayName("AddressRepository 테스트")
// class AddressRepositoryTest {
//
// 	@Autowired
// 	private AddressRepository addressRepository;
//
// 	@Autowired
// 	private TestEntityManager entityManager;
//
// 	private User testUser;
// 	private User otherUser;
// 	private Dong testDong;
// 	private Dong otherDong;
//
// 	@BeforeEach
// 	void setUp() {
// 		// 테스트 사용자 생성
// 		testUser = createUser("testUser", "password");
// 		entityManager.persist(testUser);
//
// 		otherUser = createUser("otherUser", "password");
// 		entityManager.persist(otherUser);
//
// 		Sido seoul = createSido("서울특별시", "11");
// 		entityManager.persist(seoul);
//
// 		Sigungu gangnam = createSigungu(seoul, "강남구", "680");
// 		entityManager.persist(gangnam);
//
// 		Sigungu seocho = createSigungu(seoul, "서초구", "650");
// 		entityManager.persist(seocho);
//
// 		testDong = createDong(gangnam, "102", "역삼동");
// 		entityManager.persist(testDong);
//
// 		otherDong = createDong(seocho, "103", "서초동");
// 		entityManager.persist(otherDong);
//
// 		entityManager.flush();
// 		entityManager.clear();
// 	}
//
// 	@Nested
// 	@DisplayName("findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc 메서드는")
// 	class FindAllByUserIdTest {
//
// 		@Test
// 		@DisplayName("생성일 역순으로 조회한다")
// 		void success_findAllActiveAddressesOrderByCreatedAtDesc() {
// 			// given
// 			Address address1 = createAddress(testUser, testDong, "강남구 테헤란로 1");
// 			entityManager.persist(address1);
// 			entityManager.flush();
//
// 			sleep();
//
// 			Address address2 = createAddress(testUser, testDong, "강남구 테헤란로 2");
// 			entityManager.persist(address2);
// 			entityManager.flush();
//
// 			sleep();
//
// 			Address address3 = createAddress(testUser, testDong, "강남구 테헤란로 3");
// 			entityManager.persist(address3);
// 			entityManager.flush();
// 			entityManager.clear();
//
// 			// when
// 			List<Address> addresses = addressRepository
// 				.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(testUser.getId());
//
// 			// then
// 			assertThat(addresses).hasSize(3);
// 			assertThat(addresses.get(0).getFullAddress()).isEqualTo("강남구 테헤란로 3");
// 			assertThat(addresses.get(1).getFullAddress()).isEqualTo("강남구 테헤란로 2");
// 			assertThat(addresses.get(2).getFullAddress()).isEqualTo("강남구 테헤란로 1");
//
// 			// 생성 시간 순서 확인
// 			assertThat(addresses.get(0).getCreatedAt())
// 				.isAfterOrEqualTo(addresses.get(1).getCreatedAt());
// 			assertThat(addresses.get(1).getCreatedAt())
// 				.isAfterOrEqualTo(addresses.get(2).getCreatedAt());
// 		}
//
// 		@Test
// 		@DisplayName("삭제된 주소들을 조회 결과에서 제외한다")
// 		void success_excludeDeletedAddresses() {
// 			// given
// 			Address activeAddress = createAddress(testUser, testDong, "활성 주소");
// 			entityManager.persist(activeAddress);
//
// 			Address deletedAddress = createAddress(testUser, testDong, "삭제된 주소");
// 			entityManager.persist(deletedAddress);
// 			entityManager.flush();
//
// 			// 주소 소프트 삭제
// 			deletedAddress.softDelete(testUser.getId());
// 			entityManager.flush();
// 			entityManager.clear();
//
// 			// when
// 			List<Address> addresses = addressRepository
// 				.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(testUser.getId());
//
// 			// then
// 			assertThat(addresses).hasSize(1);
// 			assertThat(addresses.get(0).getFullAddress()).isEqualTo("활성 주소");
// 			assertThat(addresses.get(0).isDeleted()).isFalse();
// 		}
//
// 		@Test
// 		@DisplayName("다른 사용자의 주소는 조회되지 않는다")
// 		void success_excludeOtherUsersAddresses() {
// 			// given
// 			Address myAddress1 = createAddress(testUser, testDong, "내 주소 1");
// 			Address myAddress2 = createAddress(testUser, testDong, "내 주소 2");
// 			Address othersAddress = createAddress(otherUser, otherDong, "다른 사람 주소");
//
// 			entityManager.persist(myAddress1);
// 			entityManager.persist(myAddress2);
// 			entityManager.persist(othersAddress);
// 			entityManager.flush();
// 			entityManager.clear();
//
// 			// when
// 			List<Address> addresses = addressRepository
// 				.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(testUser.getId());
//
// 			// then
// 			assertThat(addresses).hasSize(2);
// 			assertThat(addresses)
// 				.extracting(Address::getFullAddress)
// 				.containsExactlyInAnyOrder("내 주소 1", "내 주소 2")
// 				.doesNotContain("다른 사람 주소");
// 		}
//
// 		@Test
// 		@DisplayName("주소가 하나도 없으면 빈 리스트를 반환한다")
// 		void success_returnEmptyListWhenNoAddresses() {
// 			// when
// 			List<Address> addresses = addressRepository
// 				.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(testUser.getId());
//
// 			// then
// 			assertThat(addresses).isEmpty();
// 		}
//
// 		@Test
// 		@DisplayName("모든 주소가 삭제된 경우 빈 리스트를 반환한다")
// 		void success_returnEmptyListWhenAllAddressesDeleted() {
// 			// given
// 			Address address1 = createAddress(testUser, testDong, "주소 1");
// 			Address address2 = createAddress(testUser, testDong, "주소 2");
// 			entityManager.persist(address1);
// 			entityManager.persist(address2);
// 			entityManager.flush();
//
// 			address1.softDelete(testUser.getId());
// 			address2.softDelete(testUser.getId());
// 			entityManager.flush();
// 			entityManager.clear();
//
// 			// when
// 			List<Address> addresses = addressRepository
// 				.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(testUser.getId());
//
// 			// then
// 			assertThat(addresses).isEmpty();
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("findByIdAndDeletedAtIsNull 메서드는")
// 	class FindByIdTest {
//
// 		@Test
// 		@DisplayName("삭제되지 않은 주소를 ID로 조회한다")
// 		void success_findActiveAddressById() {
// 			// given
// 			Address address = createAddress(testUser, testDong, "서울시 강남구 역삼동 123");
// 			entityManager.persist(address);
// 			entityManager.flush();
// 			entityManager.clear();
//
// 			UUID addressId = address.getId();
//
// 			// when
// 			Optional<Address> foundAddress = addressRepository
// 				.findByIdAndDeletedAtIsNull(addressId);
//
// 			// then
// 			assertThat(foundAddress).isPresent();
// 			assertThat(foundAddress.get().getId()).isEqualTo(addressId);
// 			assertThat(foundAddress.get().getFullAddress()).isEqualTo("서울시 강남구 역삼동 123");
// 			assertThat(foundAddress.get().isDeleted()).isFalse();
// 			assertThat(foundAddress.get().getDeletedAt()).isNull();
// 			assertThat(foundAddress.get().getUser().getId()).isEqualTo(testUser.getId());
// 			assertThat(foundAddress.get().getDong().getId()).isEqualTo(testDong.getId());
// 		}
//
// 		@Test
// 		@DisplayName("삭제된 주소는 조회되지 않는다")
// 		void fail_notFindDeletedAddress() {
// 			// given
// 			Address address = createAddress(testUser, testDong, "삭제될 주소");
// 			entityManager.persist(address);
// 			entityManager.flush();
//
// 			UUID addressId = address.getId();
// 			address.softDelete(testUser.getId());
// 			entityManager.flush();
// 			entityManager.clear();
//
// 			// when
// 			Optional<Address> foundAddress = addressRepository
// 				.findByIdAndDeletedAtIsNull(addressId);
//
// 			// then
// 			assertThat(foundAddress).isEmpty();
// 		}
//
// 		@Test
// 		@DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
// 		void fail_returnEmptyForNonExistentId() {
// 			// given
// 			UUID nonExistentId = UUID.randomUUID();
//
// 			// when
// 			Optional<Address> foundAddress = addressRepository
// 				.findByIdAndDeletedAtIsNull(nonExistentId);
//
// 			// then
// 			assertThat(foundAddress).isEmpty();
// 		}
//
// 		@Test
// 		@DisplayName("null ID로 조회하면 빈 Optional을 반환한다")
// 		void fail_returnEmptyForNullId() {
// 			// when
// 			Optional<Address> foundAddress = addressRepository
// 				.findByIdAndDeletedAtIsNull(null);
//
// 			// then
// 			assertThat(foundAddress).isEmpty();
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("findByUserIdAndIsDefaultTrueAndDeletedAtIsNull 메서드는")
// 	class FindByUserIdAndIsDefaultTrueAndDeletedAtIsNullTest {
//
// 		@Test
// 		@DisplayName("삭제되지 않고 기본 주소지로 설정된 주소를 조회한다")
// 		void findDefaultAddress_Success() {
// 			// given
// 			Address address = createAddress(testUser, testDong, "서울특별시 서초구 서초동 강남대로 123");
// 			address.setDefault();
// 			addressRepository.save(address);
//
// 			// when
// 			Optional<Address> result = addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(
// 				testUser.getId());
//
// 			// then
// 			assertThat(result).isPresent();
// 			assertThat(result.get().getFullAddress()).isEqualTo("서울특별시 서초구 서초동 강남대로 123");
// 			assertThat(result.get().getIsDefault()).isTrue();
// 			assertThat(result.get().getDeletedAt()).isNull();
// 		}
//
// 		@Test
// 		@DisplayName("여러 주소 중 기본 주소지만 조회한다.")
// 		void findDefaultAddress_Success_OnlyDefaultAddress() {
// 			// given
// 			Address address1 = createAddress(testUser, testDong, "서울특별시 서초구 서초동 강남대로 123");
// 			address1.unsetDefault();
// 			addressRepository.save(address1);
//
// 			Address address2 = createAddress(testUser, testDong, "서울특별시 서초구 서초동 강남대로 456");
// 			address2.setDefault();
// 			addressRepository.save(address2);
//
// 			Address address3 = createAddress(testUser, testDong, "서울특별시 서초구 서초동 강남대로 789");
// 			address3.unsetDefault();
// 			addressRepository.save(address3);
//
// 			// when
// 			Optional<Address> result = addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(
// 				testUser.getId());
//
// 			// then
// 			assertThat(result).isPresent();
// 			assertThat(result.get().getFullAddress()).isEqualTo("서울특별시 서초구 서초동 강남대로 456");
// 			assertThat(result.get().getIsDefault()).isTrue();
// 		}
//
// 		@Test
// 		@DisplayName("삭제된 기본 주소지는 조회되지 않는다")
// 		void findDefaultAddress_Success_ExcludeDeletedAddress() {
// 			// given
// 			Address address = createAddress(testUser, testDong, "서울특별시 서초구 서초동 강남대로 123");
// 			address.setDefault();
// 			addressRepository.save(address);
//
// 			// 소프트 삭제
// 			address.softDelete(testUser.getId());
// 			addressRepository.save(address);
//
// 			// when
// 			Optional<Address> result = addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(
// 				testUser.getId());
//
// 			// then
// 			assertThat(result).isEmpty();
// 		}
//
// 		@Test
// 		@DisplayName("다른 사용자의 기본 주소지는 조회되지 않는다")
// 		void findDefaultAddress_Success_OnlyOwnAddress() {
// 			// given
// 			Address address = createAddress(otherUser, otherDong, "서울특별시 서초구 서초동 강남대로 123");
// 			address.setDefault();
// 			addressRepository.save(address);
//
// 			// when
// 			Optional<Address> result = addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(
// 				testUser.getId());
//
// 			// then
// 			assertThat(result).isEmpty();
// 		}
//
// 		@Test
// 		@DisplayName("삭제된 기본 주소와 삭제되지 않은 기본 주소가 함께 있을 때 삭제되지 않은 기본 주소만 조회한다")
// 		void findDefaultAddress_Success_ActiveDefaultOnly() {
// 			// given
// 			Address deletedAddress = createAddress(testUser, testDong, "서울특별시 서초구 서초동 강남대로 123");
// 			deletedAddress.setDefault();
// 			addressRepository.save(deletedAddress);
//
// 			deletedAddress.softDelete(testUser.getId());
// 			addressRepository.save(deletedAddress);
//
// 			// 활성 기본 주소지
// 			Address activeAddress = createAddress(testUser, testDong, "서울특별시 서초구 서초동 강남대로 456");
// 			activeAddress.setDefault();
// 			addressRepository.save(activeAddress);
//
// 			// when
// 			Optional<Address> result = addressRepository.findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(
// 				testUser.getId());
//
// 			// then
// 			assertThat(result).isPresent();
// 			assertThat(result.get().getFullAddress()).isEqualTo("서울특별시 서초구 서초동 강남대로 456");
// 			assertThat(result.get().getDeletedAt()).isNull();
// 		}
// 	}
//
// 	private User createUser(String username, String password) {
// 		return User.builder()
// 			.username(username)
// 			.password(password)
// 			.role(UserRoleEnum.CUSTOMER)
// 			.build();
// 	}
//
// 	private Address createAddress(User user, Dong dong, String address) {
// 		return Address.builder()
// 			.user(user)
// 			.dong(dong)
// 			.address(address)
// 			.build();
// 	}
//
// 	private Sido createSido(String name, String code) {
// 		return Sido.builder()
// 			.name(name)
// 			.code(code)
// 			.build();
// 	}
//
// 	private Sigungu createSigungu(Sido sido, String name, String code) {
// 		return Sigungu.builder()
// 			.sido(sido)
// 			.name(name)
// 			.code(code)
// 			.build();
// 	}
//
// 	private Dong createDong(Sigungu sigungu, String code, String name) {
// 		return Dong.builder()
// 			.sigungu(sigungu)
// 			.code(code)
// 			.name(name)
// 			.build();
// 	}
//
// 	private void sleep() {
// 		try {
// 			Thread.sleep((long)10);
// 		} catch (InterruptedException e) {
// 			Thread.currentThread().interrupt();
// 		}
// 	}
// }