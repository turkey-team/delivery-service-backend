package com.sparta.delivery.backend.review.service;

//@SpringBootTest
//@ActiveProfiles("test")
//@EnableAsync
public class ReviewRedisCacheTest {

	/*@Autowired
	private ReviewService reviewService;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private OwnerRepository ownerRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CacheManager cacheManager;

	private UUID storeId;
	private Long userId;

	private Pageable pageable;
	@Autowired
	private ReplyRepository replyRepository;

	@BeforeEach
	void setUp() {
		pageable = PageRequest.of(0, 10);

		// 테스트용 인증 사용자 생성
		User user = new User();
		user.setUsername("user1");
		user.setPassword("password");
		user.setRole(UserRoleEnum.CUSTOMER);
		user = userRepository.save(user);

		userId = user.getId();

		// Spring SecurityContext에 인증 정보 등록
		UserDetailsImpl userDetails = new UserDetailsImpl(user); // User를 감싸는 UserDetailsImpl 생성
		Authentication authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		Owner owner = new Owner();
		owner.setUser(user);
		owner.setNickname("테스트 사장님");
		ownerRepository.save(owner);

		Store store = new Store();
		//store.setId(storeId);
		store.setName("테스트 가게");
		store.setOwner(owner);
		storeRepository.save(store);
		storeId = store.getId();

		Customer customer = new Customer();
		customer.setUser(user);
		customer.setNickname("테스트 사용자");
		customerRepository.save(customer);

		Review review = Review.builder()
			.customer(customer)
			.store(store)
			.context("내용")
			.rate(5)
			.imageUrl(null)
			.build();

		Review review2 = Review.builder()
			.customer(customer)
			.store(store)
			.context("내용2")
			.rate(4)
			.imageUrl(null)
			.build();

		reviewRepository.save(review);
		reviewRepository.save(review2);

		// 테스트 전 캐시 초기화
		Cache cache = cacheManager.getCache("reviewList");
		if (cache != null) {
			cache.clear();
		}
	}

	// 리뷰 리스트 + Redis 적용 테스트
	@Test
	void cacheableTest() {
		Cache cache = cacheManager.getCache("reviewList");
		assertNotNull(cache);

		String key =
			"review:store:" + storeId;

		// 캐시 없는 상태에서 조회 호출
		ReviewRepositorySearchConditionDto condition = new ReviewRepositorySearchConditionDto();
		condition.setContext("");
		reviewService.getReviews(storeId, condition, pageable);

		// 캐시에 데이터가 저장되었는지 확인
		Cache.ValueWrapper cachedWrapper = cache.get(key);
		assertNotNull(cache.get(key), "캐시에 데이터가 저장되어야 함");

		System.out.println("캐시 key: " + key);
		System.out.println("캐시 값: " + cachedWrapper.get());
	}

	private void printCacheStatus(Cache cache, String key, String stage) {
		Cache.ValueWrapper wrapper = cache.get(key);
		if (wrapper == null) {
			System.out.println("[" + stage + "] 캐시 없음 → key=" + key);
		} else {
			System.out.println("[" + stage + "] 캐시 존재 → key=" + key + ", value=" + wrapper.get());
		}
	}

	// 리뷰 등록 수정 삭제 + Cache Evict 적용 테스트
	@Test
	void cacheEvictOnReviewCreateUpdateDelete() throws InterruptedException {
		Cache cache = cacheManager.getCache("reviewList");
		String key =
			"review:store:" + storeId;

		Order order = new Order();
		order.setCustomer(customerRepository.findAll().get(0));
		order.setOrderStatus(OrderStatus.SUCCESS); // 리뷰 작성 가능 상태
		orderRepository.save(order);
		UUID orderId = order.getId();

		// 캐시 없는 상태에서 조회 → 캐시 생성
		ReviewRepositorySearchConditionDto condition = new ReviewRepositorySearchConditionDto();
		condition.setContext("내용");
		reviewService.getReviews(storeId, condition, pageable);

		printCacheStatus(cache, key, "조회 후 캐시");
		assertNotNull(cache.get(key), "조회 후 캐시가 생성되어야 함");

		// 리뷰 등록 → 캐시 무효화 확인
		ReqCreateReviewDto newReview = new ReqCreateReviewDto();
		newReview.setContext("너는 이걸 짬뽕이라고 만들어놓았냐? 맛도 싱겁고 국수도 다 안 익은채로 왔잖아!");
		newReview.setRate(4);
		newReview.setOrderId(orderId);
		ResResultReviewDto resResultReviewDto = reviewService.registerReview(newReview, storeId, userId);
		System.out.println("등록된 리뷰 = " + resResultReviewDto);
		printCacheStatus(cache, key, "리뷰 등록 후 캐시");
		assertNull(cache.get(key), "리뷰 등록 후 캐시는 무효화되어야 함");

		await().atMost(10, TimeUnit.SECONDS).until(() ->
			!replyRepository.findByReviewId(resResultReviewDto.getReviewId()).isEmpty()
		);

		List<Reply> replies = replyRepository.findByReviewId(resResultReviewDto.getReviewId());
		System.out.println("autoReply = " + replies.get(0).getContext());

		// 캐시 다시 조회 → 캐시 재생성
		reviewService.getReviews(storeId, condition, pageable);
		printCacheStatus(cache, key, "재조회 후 캐시");
		assertNotNull(cache.get(key), "재조회 후 캐시가 다시 생성되어야 함");

		//리뷰 수정 → 캐시 무효화 확인
		Review review = reviewRepository.findAll().get(0);
		ReqUpdateReviewDto updateDto = new ReqUpdateReviewDto();
		updateDto.setContext("수정 리뷰");
		updateDto.setRate(5);
		ResResultReviewDto dto = reviewService.updateReview(updateDto, review.getId(), userId);
		System.out.println("수정된 리뷰 = " + dto);
		printCacheStatus(cache, key, "리뷰 수정 후 캐시");
		assertNull(cache.get(key), "리뷰 수정 후 캐시는 무효화되어야 함");

		// 리뷰 삭제 → 캐시 무효화 확인
		ReqDeleteReviewDto reqDeleteReviewDto = reviewService.deleteReview(review.getId(), userId);
		System.out.println("삭제된 리뷰 = " + reqDeleteReviewDto);
		printCacheStatus(cache, key, "리뷰 삭제 후 캐시");
		assertNull(cache.get(key), "리뷰 삭제 후 캐시는 무효화되어야 함");
	}

	// Order가 완료되지 않았을때 예외 테스트
	@Test
	void registerReview_withIncompleteOrder_shouldThrowException() {
		// 배송 완료가 아닌 주문 생성
		Order incompleteOrder = new Order();
		incompleteOrder.setCustomer(customerRepository.findAll().get(0));
		incompleteOrder.setOrderStatus(OrderStatus.ORDERING); // 미완료 상태
		orderRepository.save(incompleteOrder);

		ReqCreateReviewDto reviewDto = new ReqCreateReviewDto();
		reviewDto.setContext("리뷰 내용");
		reviewDto.setRate(5);
		reviewDto.setOrderId(incompleteOrder.getId());

		// 예외 발생 확인
		IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
			reviewService.registerReview(reviewDto, storeId, userId)
		);

		System.out.println("예외 메시지: " + exception.getMessage());
		assertEquals("배송 완료된 주문만 리뷰 작성 가능", exception.getMessage());
	}

	// 주문을 한 사용자가 아닌 다른 사람이 리뷰 작성하는 예외 테스트
	@Test
	void registerReview_withWrongCustomer_shouldThrowException() {
		// 다른 사용자 생성
		User otherUser = new User();
		otherUser.setUsername("otherUser");
		otherUser.setPassword("password");
		otherUser.setRole(UserRoleEnum.CUSTOMER);
		userRepository.save(otherUser);

		Customer otherCustomer = new Customer();
		otherCustomer.setUser(otherUser);
		otherCustomer.setNickname("다른 고객");
		customerRepository.save(otherCustomer);

		// 다른 고객으로 주문 생성
		Order orderByOther = new Order();
		orderByOther.setCustomer(otherCustomer);
		orderByOther.setOrderStatus(OrderStatus.SUCCESS);
		orderRepository.save(orderByOther);

		ReqCreateReviewDto reviewDto = new ReqCreateReviewDto();
		reviewDto.setContext("리뷰 내용");
		reviewDto.setRate(5);
		reviewDto.setOrderId(orderByOther.getId());

		// 예외 발생 확인
		UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
			reviewService.registerReview(reviewDto, storeId, userId) // 원래 userId로 시도
		);

		System.out.println("예외 메시지: " + exception.getMessage());
		assertEquals("주문한 고객만 리뷰 작성 가능", exception.getMessage());
	}

	// 다른 사용자가 내 리뷰 수정 예외 테스트
	@Test
	void updateReview_byOtherCustomer_shouldThrowException() {
		// 다른 사용자 생성
		final User otherUser = new User();
		otherUser.setUsername("otherUser");
		otherUser.setPassword("password");
		otherUser.setRole(UserRoleEnum.CUSTOMER);
		userRepository.save(otherUser);

		Customer otherCustomer = new Customer();
		otherCustomer.setUser(otherUser);
		otherCustomer.setNickname("다른 고객");
		customerRepository.save(otherCustomer);

		// 기존 리뷰 가져오기
		final Review existingReview = reviewRepository.findAll().get(0);

		// 리뷰 수정 DTO
		ReqUpdateReviewDto updateDto = new ReqUpdateReviewDto();
		updateDto.setContext("변경 시도");
		updateDto.setRate(3);

		// 다른 사용자로 리뷰 수정 시도 → UnauthorizedException 발생
		UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
			reviewService.updateReview(updateDto, existingReview.getId(), otherUser.getId())
		);

		System.out.println("예외 메시지: " + exception.getMessage());
		assertEquals("본인이 작성한 리뷰만 수정 가능합니다.", exception.getMessage());
	}

	// 다른 사용자가 내 리뷰 삭제 예외 테스트
	@Test
	void deleteReview_byOtherCustomer_shouldThrowException() {
		// 다른 사용자 생성
		final User otherUser = new User();
		otherUser.setUsername("otherUser");
		otherUser.setPassword("password");
		otherUser.setRole(UserRoleEnum.CUSTOMER);
		userRepository.save(otherUser);

		Customer otherCustomer = new Customer();
		otherCustomer.setUser(otherUser);
		otherCustomer.setNickname("다른 고객");
		customerRepository.save(otherCustomer);

		// 기존 리뷰 가져오기
		final Review existingReview = reviewRepository.findAll().get(0);

		// 다른 사용자로 리뷰 삭제 시도 → UnauthorizedException 발생
		UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
			reviewService.deleteReview(existingReview.getId(), otherUser.getId())
		);

		System.out.println("예외 메시지: " + exception.getMessage());
		assertEquals("본인이 작성한 리뷰만 삭제 가능합니다.", exception.getMessage());
	}*/

}