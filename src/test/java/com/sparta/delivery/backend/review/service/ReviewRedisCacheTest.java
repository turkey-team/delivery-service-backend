package com.sparta.delivery.backend.review.service;

//@SpringBootTest
//@ActiveProfiles("test")
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

	private Pageable pageable;

	@BeforeEach
	void setUp() {
		pageable = PageRequest.of(0, 10);

		// 테스트용 인증 사용자 생성
		User user = new User();
		user.setUsername("user1");
		user.setPassword("password");
		user.setRole(UserRoleEnum.CUSTOMER);
		user = userRepository.save(user);

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

	@Test
	void cacheableTest() {
		Cache cache = cacheManager.getCache("reviewList");
		assertNotNull(cache);

		String key =
			"review:store:" + storeId;

		// 캐시 없는 상태에서 조회 호출
		ReviewRepositorySearchConditionDto condition = new ReviewRepositorySearchConditionDto();
		condition.setContext("내용");
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

	@Test
	void cacheEvictOnReviewCreateUpdateDelete() {
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
		newReview.setContext("새 리뷰");
		newReview.setRate(4);
		reviewService.registerReview(newReview, storeId, orderId);
		printCacheStatus(cache, key, "리뷰 등록 후 캐시");
		assertNull(cache.get(key), "리뷰 등록 후 캐시는 무효화되어야 함");

		// 캐시 다시 조회 → 캐시 재생성
		reviewService.getReviews(storeId, condition, pageable);
		printCacheStatus(cache, key, "재조회 후 캐시");
		assertNotNull(cache.get(key), "재조회 후 캐시가 다시 생성되어야 함");

		//리뷰 수정 → 캐시 무효화 확인
		Review review = reviewRepository.findAll().get(0);
		ReqUpdateReviewDto updateDto = new ReqUpdateReviewDto();
		updateDto.setContext("수정 리뷰");
		updateDto.setRate(5);
		reviewService.updateReview(updateDto, review.getId(), storeId);
		printCacheStatus(cache, key, "리뷰 수정 후 캐시");
		assertNull(cache.get(key), "리뷰 수정 후 캐시는 무효화되어야 함");

		// 리뷰 삭제 → 캐시 무효화 확인
		reviewService.deleteReview(review.getId(), storeId);
		printCacheStatus(cache, key, "리뷰 삭제 후 캐시");
		assertNull(cache.get(key), "리뷰 삭제 후 캐시는 무효화되어야 함");
	}*/

}