package com.sparta.delivery.backend.review.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.order.repository.OrderRepository;
import com.sparta.delivery.backend.review.service.ReviewService;
import com.sparta.delivery.backend.store.repository.StoreRepository;

@ExtendWith(MockitoExtension.class)
class ReviewRepositoryTest {

	@InjectMocks
	private ReviewService reviewService;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private ImageRepository imageRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Test
	void testFindStoresWithAverageRating() {
		// ===== 1. Mock 데이터 준비 =====
		/*UUID store1Id = UUID.randomUUID();
		UUID store2Id = UUID.randomUUID();

		// store1 리뷰 2개: 5, 3 → 평균 4.0
		List<Integer> store1Rates = Arrays.asList(5, 5);
		double store1Average = store1Rates.stream().mapToInt(Integer::intValue).average().orElse(0);

		// store2 리뷰 3개: 4, 5, 3 → 평균 4.0
		List<Integer> store2Rates = Arrays.asList(4, 5, 3);
		double store2Average = store2Rates.stream().mapToInt(Integer::intValue).average().orElse(0);

		StoreAverageRatingDto store1Dto = new StoreAverageRatingDto(store1Id, "가게1", store1Average);
		StoreAverageRatingDto store2Dto = new StoreAverageRatingDto(store2Id, "가게2", store2Average);

		when(reviewRepository.findStoresWithAverageRating())
			.thenReturn(Arrays.asList(store1Dto, store2Dto));

		// ===== 2. Service 호출 (Repository Mock 사용) =====
		List<StoreAverageRatingDto> results = reviewRepository.findStoresWithAverageRating();

		// ===== 3. 결과 검증 =====
		assertEquals(2, results.size());

		assertTrue(results.stream().anyMatch(s -> s.getStoreId().equals(store1Id) && s.getRate() == 5.0));
		assertTrue(results.stream().anyMatch(s -> s.getStoreId().equals(store2Id) && s.getRate() == 4.0));
		// ===== 4. Repository 호출 검증 =====
		verify(reviewRepository, times(1)).findStoresWithAverageRating();*/
	}

	@Test
	void testRegisterSingleReview() {
		// ===== 1. 테스트용 엔티티 생성 =====
		/*UUID userId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();
		UUID storeId = UUID.randomUUID();
		UUID orderId = UUID.randomUUID();
		UUID imageId = UUID.randomUUID();

		User testUser = User.builder()
			.username("testUser")
			.password("password")
			.role(UserRole.CUSTOMER)
			.build();
		// User ID 직접 세팅
		testUser.setId(1L);

		Customer customer = Customer.builder()
			.user(testUser)
			.nickname("tester")
			.email("tester@example.com")
			.phoneNumber("010-1234-5678")
			.build();
		customer.setId(customerId);

		Store store = new Store();
		store.setId(storeId);

		Order order = new Order();
		order.setCustomer(customer);
		order.setStore(store);
		order.setOrderStatus(OrderStatus.SUCCESS);
		order.setId(orderId);

		Image image = Image.builder()
			.imageUrl("http://example.com/image.jpg")
			.build();
		image.setId(imageId);

		// ===== 2. DTO 생성 =====
		ReviewRegisterDto dto = new ReviewRegisterDto();
		dto.setContext("맛있어요!");
		dto.setRate(5);
		dto.setImageId(imageId);

		// ===== 3. Mockito 동작 정의 =====
		when(customerRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(customer));
		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
		when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));
		when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// ===== 4. 서비스 호출 =====
		ReviewResponseDto response = reviewService.registerReview(dto, storeId, orderId, testUser);

		// ===== 5. 결과 검증 =====
		assertNotNull(response);
		assertEquals("맛있어요!", response.getContext());
		assertEquals(5, response.getRate());
		assertEquals("http://example.com/image.jpg", response.getImageUrl());*/
	}

}