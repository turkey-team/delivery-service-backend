package com.sparta.delivery.backend.review.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.order.entity.Order;
import com.sparta.delivery.backend.order.repository.OrderRepository;
import com.sparta.delivery.backend.review.repository.ReviewRepository;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@InjectMocks
	private ReviewService reviewService;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private StoreRepository storeRepository;

	private User testUser;
	private Customer testCustomer;
	private Store testStore;
	private Order testOrder;

	private Long testUserId = 1L;

	/*@BeforeEach
	void setUp() {
		// 테스트용 User / Customer / Store / Order 생성
		UUID testId = UUID.randomUUID();
		testUser = new User();
		testUser.setId(testUserId);
		testUser.setUsername("testUser");
		testUser.setPassword("pw");
		testUser.setRole(UserRoleEnum.CUSTOMER);

		testCustomer = new Customer();
		testCustomer.setId(UUID.randomUUID());
		testCustomer.setUser(testUser);

		testStore = new Store();
		testStore.setId(UUID.randomUUID());
		testStore.setName("테스트 가게");

		testOrder = new Order();
		testOrder.setId(UUID.randomUUID());
		testOrder.setCustomer(testCustomer);
		testOrder.setStore(testStore);
		testOrder.setOrderStatus(OrderStatus.SUCCESS);

		// SecurityContext에 로그인 정보 설정
		UserDetailsImpl userDetails = new UserDetailsImpl(testUser);
		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	// ==================== 리뷰 등록 ====================
	@Test
	void testRegisterReview_authenticatedUser() {
		ReqCreateReviewDto dto = new ReqCreateReviewDto("리뷰 내용", 5, null);

		when(customerRepository.findByUserId(testUserId)).thenReturn(Optional.of(testCustomer));
		when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
		when(storeRepository.findById(testStore.getId())).thenReturn(Optional.of(testStore));
		when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArguments()[0]);

		ResResultReviewDto result = reviewService.registerReview(dto, testStore.getId(), testOrder.getId());

		assertNotNull(result);
		assertEquals("리뷰 내용", result.getContext());
		assertEquals(5, result.getRate());

		System.out.println("✅ 리뷰 등록 결과 DTO: " + result);
	}

	// 다른 사람이 리뷰 등록하려할때
	@Test
	void testRegisterReview_notAuthenticatedUser_throwsException() {

		// given
		ReqCreateReviewDto dto = new ReqCreateReviewDto("리뷰 내용", 5, null);

		// 다른 사용자(Customer)를 Mock으로 반환
		Customer otherCustomer = new Customer();
		otherCustomer.setId(UUID.randomUUID());
		User otherUser = new User();
		otherUser.setId(999L); // 로그인된 사용자 ID(testUserId)와 다른 값
		otherCustomer.setUser(otherUser);

		when(customerRepository.findByUserId(testUserId))
			.thenReturn(Optional.of(otherCustomer)); // 로그인한 사용자의 Customer가 아닌 다른 사용자를 반환
		when(orderRepository.findById(testOrder.getId()))
			.thenReturn(Optional.of(testOrder));

		// when & then
		assertThrows(UnauthorizedException.class,
			() -> reviewService.registerReview(dto, testStore.getId(), testOrder.getId()));
	}

	// ==================== 리뷰 수정 ====================
	@Test
	void testUpdateReview_authenticatedUser() {
		UUID reviewId = UUID.randomUUID();
		Review existingReview = Review.builder()
			.customer(testCustomer)
			.store(testStore)
			.context("기존 리뷰")
			.rate(3)
			.imageUrl("http://image.com")
			.build();

		ReqUpdateReviewDto updateDto = new ReqUpdateReviewDto("수정된 리뷰", 4, "http://image1.com");

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
		when(customerRepository.findByUserId(testUserId)).thenReturn(Optional.of(testCustomer));
		when(storeRepository.findById(testStore.getId())).thenReturn(Optional.of(testStore));

		ResResultReviewDto updated = reviewService.updateReview(updateDto, reviewId);

		assertEquals("수정된 리뷰", updated.getContext());
		assertEquals(4, updated.getRate());

		System.out.println("updated = " + updated);
	}

	// ==================== 리뷰 삭제 ====================
	@Test
	void testDeleteReview_authenticatedUser() {
		UUID reviewId = UUID.randomUUID();
		Review existingReview = Review.builder()
			.customer(testCustomer)
			.store(testStore)
			.context("삭제할 리뷰")
			.rate(5)
			.build();

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
		when(customerRepository.findByUserId(testUserId)).thenReturn(Optional.of(testCustomer));
		when(storeRepository.findById(testStore.getId())).thenReturn(Optional.of(testStore));

		ReqDeleteReviewDto deleted = reviewService.deleteReview(reviewId);

		assertNotNull(deleted);
		// Soft delete 여부 확인
		assertNotNull(deleted.getDeletedAt());

		System.out.println("deleted = " + deleted);
	}

	// ==================== 권한 없는 사용자 예외 ====================
	@Test
	void testUpdateReview_notAuthor_throwsException() {
		UUID reviewId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();

		Customer otherCustomer = new Customer();
		otherCustomer.setId(customerId);

		Review existingReview = new Review();
		existingReview.setCustomer(otherCustomer); // 다른 사용자 작성
		existingReview.setStore(testStore);
		existingReview.setContext("기존 리뷰");
		existingReview.setRate(3);

		ReqUpdateReviewDto updateDto = new ReqUpdateReviewDto("수정된 리뷰", 4, null);

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
		when(customerRepository.findByUserId(testUserId)).thenReturn(Optional.of(testCustomer));

		assertThrows(UnauthorizedException.class,
			() -> reviewService.updateReview(updateDto, reviewId));
	}

	@Test
	void testDeleteReview_notAuthor_throwsException() {
		UUID reviewId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();

		Customer otherCustomer = new Customer();
		otherCustomer.setId(customerId);

		Review existingReview = new Review();
		existingReview.setCustomer(otherCustomer); // 다른 사용자 작성
		existingReview.setStore(testStore);
		existingReview.setContext("삭제할 리뷰");
		existingReview.setRate(5);

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
		when(customerRepository.findByUserId(testUserId)).thenReturn(Optional.of(testCustomer));

		assertThrows(UnauthorizedException.class,
			() -> reviewService.deleteReview(reviewId));
	}*/

}