package com.sparta.delivery.backend.customer.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.address.repository.AddressRepository;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.entity.CustomerAddress;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.region.repository.SidoRepository;
import com.sparta.delivery.backend.region.repository.SigunguRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;
import com.sparta.delivery.backend.user.repository.UserRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
public class CustomerAddressRepositoryTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private CustomerAddressRepository customerAddressRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DongRepository dongRepository;

	@Autowired
	private SigunguRepository sigunguRepository;

	@Autowired
	private SidoRepository sidoRepository;

	private Customer customer;
	private Address address1;
	private Address address2;
	private Dong dong;

	@BeforeEach
	void setUp() {
		// Sido 생성
		Sido sido = Sido.builder()
			.name("서울특별시")
			.code("11")
			.build();
		sido = sidoRepository.save(sido);

		// Sigungu 생성
		Sigungu sigungu = Sigungu.builder()
			.sido(sido)
			.name("종로구")
			.code("110")
			.build();
		sigungu = sigunguRepository.save(sigungu);

		// Dong 생성
		dong = Dong.builder()
			.sigungu(sigungu)
			.name("묘동")
			.code("103")
			.build();
		dong = dongRepository.save(dong);

		// User 생성
		User user = User.builder()
			.username("testuser")
			.password("encodedPassword")
			.role(UserRoleEnum.CUSTOMER)
			.build();
		user = userRepository.save(user);

		// Customer 생성
		customer = Customer.builder()
			.user(user)
			.nickname("테스트고객")
			.email("test@example.com")
			.phoneNumber("01012345678")
			.build();
		customer = customerRepository.save(customer);

		// Address 생성
		address1 = Address.builder()
			.dong(dong)
			.fullAddress("서울특별시 종로구 돈화문로 27")
			.build();
		address1 = addressRepository.save(address1);

		address2 = Address.builder()
			.dong(dong)
			.fullAddress("서울특별시 종로구 돈화문로 30")
			.build();
		address2 = addressRepository.save(address2);
	}

	@Nested
	@DisplayName("고객의 모든 배송지 조회 테스트")
	class FindAllByCustomerTest {

		@Test
		@DisplayName("성공 - 기본 배송지 우선, 최신순 정렬")
		void success_orderByDefaultAndCreatedAt() {
			// given
			CustomerAddress ca1 = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(false)
				.nickname("첫번째")
				.build();
			customerAddressRepository.save(ca1);

			CustomerAddress ca2 = CustomerAddress.builder()
				.customer(customer)
				.address(address2)
				.isDefault(true)
				.nickname("두번째(기본)")
				.build();
			customerAddressRepository.save(ca2);

			// when
			List<CustomerAddress> result = customerAddressRepository
				.findAllByCustomerAndDeletedAtIsNullOrderByIsDefaultDescCreatedAtDesc(customer);

			// then
			assertThat(result).hasSize(2);
			assertThat(result.get(0).getIsDefault()).isTrue();
			assertThat(result.get(0).getNickname()).isEqualTo("두번째(기본)");
			assertThat(result.get(1).getIsDefault()).isFalse();
		}

		@Test
		@DisplayName("성공 - 배송지 없음")
		void success_emptyList() {
			// when
			List<CustomerAddress> result = customerAddressRepository
				.findAllByCustomerAndDeletedAtIsNullOrderByIsDefaultDescCreatedAtDesc(customer);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("성공 - 삭제된 배송지는 제외")
		void success_excludeDeleted() {
			// given
			CustomerAddress ca1 = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(false)
				.nickname("정상배송지")
				.build();
			customerAddressRepository.save(ca1);

			CustomerAddress ca2 = CustomerAddress.builder()
				.customer(customer)
				.address(address2)
				.isDefault(false)
				.nickname("삭제된배송지")
				.build();
			ca2 = customerAddressRepository.save(ca2);
			ca2.softDelete(1L);

			// when
			List<CustomerAddress> result = customerAddressRepository
				.findAllByCustomerAndDeletedAtIsNullOrderByIsDefaultDescCreatedAtDesc(customer);

			// then
			assertThat(result).hasSize(1);
			assertThat(result.get(0).getNickname()).isEqualTo("정상배송지");
		}

		@Test
		@DisplayName("성공 - Fetch Join으로 N+1 방지")
		void success_fetchJoin() {
			// given
			CustomerAddress ca = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(true)
				.nickname("테스트")
				.build();
			customerAddressRepository.save(ca);

			// when
			List<CustomerAddress> result = customerAddressRepository
				.findAllByCustomerAndDeletedAtIsNullOrderByIsDefaultDescCreatedAtDesc(customer);

			// then
			assertThat(result).hasSize(1);
			// Address와 Dong이 이미 fetch되어 있어야 함 (lazy loading 발생 안 함)
			assertThat(result.get(0).getAddress()).isNotNull();
			assertThat(result.get(0).getAddress().getDong()).isNotNull();
		}
	}

	@Nested
	@DisplayName("기본 배송지 조회 테스트")
	class FindByCustomerAndIsDefaultTrueTest {

		@Test
		@DisplayName("성공 - 기본 배송지 조회")
		void success() {
			// given
			CustomerAddress ca = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(true)
				.nickname("기본배송지")
				.build();
			customerAddressRepository.save(ca);

			// when
			Optional<CustomerAddress> result = customerAddressRepository
				.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer);

			// then
			assertThat(result).isPresent();
			assertThat(result.get().getIsDefault()).isTrue();
			assertThat(result.get().getNickname()).isEqualTo("기본배송지");
		}

		@Test
		@DisplayName("성공 - 기본 배송지 없음")
		void success_notFound() {
			// given
			CustomerAddress ca = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(false)
				.nickname("일반배송지")
				.build();
			customerAddressRepository.save(ca);

			// when
			Optional<CustomerAddress> result = customerAddressRepository
				.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("성공 - 삭제된 기본 배송지는 제외")
		void success_excludeDeleted() {
			// given
			CustomerAddress ca = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(true)
				.nickname("삭제된기본배송지")
				.build();
			ca = customerAddressRepository.save(ca);
			ca.softDelete(1L);

			// when
			Optional<CustomerAddress> result = customerAddressRepository
				.findByCustomerAndIsDefaultTrueAndDeletedAtIsNull(customer);

			// then
			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("ID로 배송지 조회 테스트")
	class FindByIdTest {

		@Test
		@DisplayName("성공 - ID로 배송지 조회")
		void success() {
			// given
			CustomerAddress ca = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(true)
				.nickname("테스트배송지")
				.build();
			ca = customerAddressRepository.save(ca);
			UUID id = ca.getId();

			// when
			Optional<CustomerAddress> result = customerAddressRepository.findByIdAndDeletedAtIsNull(id);

			// then
			assertThat(result).isPresent();
			assertThat(result.get().getId()).isEqualTo(id);
			assertThat(result.get().getNickname()).isEqualTo("테스트배송지");
		}

		@Test
		@DisplayName("성공 - 존재하지 않는 ID")
		void success_notFound() {
			// when
			Optional<CustomerAddress> result = customerAddressRepository
				.findByIdAndDeletedAtIsNull(UUID.randomUUID());

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("성공 - 삭제된 배송지는 조회 안 됨")
		void success_deletedNotFound() {
			// given
			CustomerAddress ca = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(true)
				.nickname("삭제된배송지")
				.build();
			ca = customerAddressRepository.save(ca);
			UUID id = ca.getId();
			ca.softDelete(1L);

			// when
			Optional<CustomerAddress> result = customerAddressRepository.findByIdAndDeletedAtIsNull(id);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("성공 - Fetch Join으로 Address와 Dong도 조회")
		void success_fetchJoin() {
			// given
			CustomerAddress ca = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(true)
				.nickname("테스트")
				.build();
			ca = customerAddressRepository.save(ca);

			// when
			Optional<CustomerAddress> result = customerAddressRepository.findByIdAndDeletedAtIsNull(ca.getId());

			// then
			assertThat(result).isPresent();
			assertThat(result.get().getAddress()).isNotNull();
			assertThat(result.get().getAddress().getDong()).isNotNull();
			assertThat(result.get().getAddress().getDong().getName()).isEqualTo("묘동");
		}
	}

	@Nested
	@DisplayName("Address 사용 여부 확인 테스트")
	class ExistsByAddressTest {

		@Test
		@DisplayName("성공 - Address를 사용하는 CustomerAddress 있음")
		void success_exists() {
			// given
			CustomerAddress ca = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(true)
				.nickname("테스트")
				.build();
			customerAddressRepository.save(ca);

			// when
			boolean result = customerAddressRepository.existsByAddressAndDeletedAtIsNull(address1);

			// then
			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("성공 - Address를 사용하는 CustomerAddress 없음")
		void success_notExists() {
			// when
			boolean result = customerAddressRepository.existsByAddressAndDeletedAtIsNull(address1);

			// then
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("성공 - 삭제된 CustomerAddress는 카운트 안 함")
		void success_excludeDeleted() {
			// given
			CustomerAddress ca = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(true)
				.nickname("삭제된배송지")
				.build();
			ca = customerAddressRepository.save(ca);
			ca.softDelete(1L);

			// when
			boolean result = customerAddressRepository.existsByAddressAndDeletedAtIsNull(address1);

			// then
			assertThat(result).isFalse();
		}

		@Test
		@DisplayName("성공 - 여러 CustomerAddress가 같은 Address 사용")
		void success_multipleCustomerAddresses() {
			// given
			User user2 = User.builder()
				.username("testuser2")
				.password("password")
				.role(UserRoleEnum.CUSTOMER)
				.build();
			user2 = userRepository.save(user2);

			Customer customer2 = Customer.builder()
				.user(user2)
				.nickname("테스트고객2")
				.email("test2@example.com")
				.phoneNumber("01087654321")
				.build();
			customer2 = customerRepository.save(customer2);

			CustomerAddress ca1 = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(true)
				.nickname("고객1배송지")
				.build();
			customerAddressRepository.save(ca1);

			CustomerAddress ca2 = CustomerAddress.builder()
				.customer(customer2)
				.address(address1)  // 같은 Address 사용
				.isDefault(true)
				.nickname("고객2배송지")
				.build();
			customerAddressRepository.save(ca2);

			// when
			boolean result = customerAddressRepository.existsByAddressAndDeletedAtIsNull(address1);

			// then
			assertThat(result).isTrue();
		}
	}

	@Nested
	@DisplayName("고객의 배송지 개수 조회 테스트")
	class CountByCustomerTest {

		@Test
		@DisplayName("성공 - 배송지 개수 조회")
		void success() {
			// given
			CustomerAddress ca1 = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(true)
				.nickname("첫번째")
				.build();
			customerAddressRepository.save(ca1);

			CustomerAddress ca2 = CustomerAddress.builder()
				.customer(customer)
				.address(address2)
				.isDefault(false)
				.nickname("두번째")
				.build();
			customerAddressRepository.save(ca2);

			// when
			long count = customerAddressRepository.countByCustomerAndDeletedAtIsNull(customer);

			// then
			assertThat(count).isEqualTo(2);
		}

		@Test
		@DisplayName("성공 - 배송지 없음")
		void success_zero() {
			// when
			long count = customerAddressRepository.countByCustomerAndDeletedAtIsNull(customer);

			// then
			assertThat(count).isEqualTo(0);
		}

		@Test
		@DisplayName("성공 - 삭제된 배송지는 카운트 안 함")
		void success_excludeDeleted() {
			// given
			CustomerAddress ca1 = CustomerAddress.builder()
				.customer(customer)
				.address(address1)
				.isDefault(true)
				.nickname("정상배송지")
				.build();
			customerAddressRepository.save(ca1);

			CustomerAddress ca2 = CustomerAddress.builder()
				.customer(customer)
				.address(address2)
				.isDefault(false)
				.nickname("삭제된배송지")
				.build();
			ca2 = customerAddressRepository.save(ca2);
			ca2.softDelete(1L);

			// when
			long count = customerAddressRepository.countByCustomerAndDeletedAtIsNull(customer);

			// then
			assertThat(count).isEqualTo(1);
		}
	}

	@Nested
	@DisplayName("Cascade 테스트")
	class CascadeTest {

		@Test
		@DisplayName("성공 - Customer에 추가 시 CustomerAddress와 Address 자동 저장")
		void success_cascadePersist() {
			// given
			Address newAddress = Address.builder()
				.dong(dong)
				.fullAddress("서울특별시 종로구 돈화문로 100")
				.build();

			CustomerAddress newCustomerAddress = CustomerAddress.builder()
				.customer(customer)
				.address(newAddress)
				.isDefault(true)
				.nickname("새배송지")
				.build();

			// when
			customer.addCustomerAddress(newCustomerAddress);

			em.flush();

			// then - CustomerAddress와 Address가 자동으로 저장되어야 함
			List<CustomerAddress> result = customerAddressRepository
				.findAllByCustomerAndDeletedAtIsNullOrderByIsDefaultDescCreatedAtDesc(customer);

			assertThat(result).hasSize(1);
			assertThat(result.get(0).getNickname()).isEqualTo("새배송지");
			assertThat(result.get(0).getAddress()).isNotNull();
			assertThat(result.get(0).getAddress().getFullAddress()).isEqualTo("서울특별시 종로구 돈화문로 100");
		}
	}
}
