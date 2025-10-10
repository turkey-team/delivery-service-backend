package com.sparta.delivery.backend.reply.service;

//@ExtendWith(MockitoExtension.class)
class ReplyServiceTest {

	/*@InjectMocks
	private ReplyService replyService;

	@Mock
	private ReplyRepository replyRepository;

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private ManagerRepository managerRepository;

	@Mock
	private ReviewRepository reviewRepository;

	private User ownerUser;
	private Owner testOwner;
	private User managerUser;
	private Manager testManager;
	private Store testStore;
	private Review testReview;

	private User customerUser;
	private Customer testCustomer;

	private Long testOwnerId = 1L;
	private Long testManagerId = 2L;
	private Long testCustomerId = 3L;

	@BeforeEach
	void setUp() {
		// Owner User & Owner
		ownerUser = new User();
		ownerUser.setId(testOwnerId);
		ownerUser.setUsername("testOwner");
		ownerUser.setRole(UserRoleEnum.OWNER);

		testOwner = Owner.builder()
			.user(ownerUser)
			.nickname("테스트닉네임")
			.email("owner@test.com")
			.phoneNumber("010-1111-2222")
			.businessNumber("123-45-67890")
			.build();
		testOwner.setId(UUID.randomUUID());

		// Manager User & Manager
		managerUser = new User();
		managerUser.setId(testManagerId);
		managerUser.setUsername("testManager");
		managerUser.setRole(UserRoleEnum.MANAGER);

		testManager = Manager.builder()
			.user(managerUser)
			.name("매니저닉네임")
			.email("manager@test.com")
			.phoneNumber("010-2222-3333")
			.build();
		testManager.setId(UUID.randomUUID());

		customerUser = new User();
		customerUser.setId(testCustomerId);
		customerUser.setUsername("testCustomer");
		customerUser.setRole(UserRoleEnum.CUSTOMER);

		testCustomer = Customer.builder()
			.user(customerUser)
			.nickname("테스트고객")
			.email("customer@test.com")
			.phoneNumber("010-3333-4444")
			.build();
		testCustomer.setId(UUID.randomUUID());

		// Store & Review
		testStore = new Store();
		testStore.setOwner(testOwner);
		testStore.setName("테스트가게");

		testReview = Review.builder()
			.customer(null)
			.store(testStore)
			.context("리뷰 내용")
			.rate(5)
			.build();
		testReview.setId(UUID.randomUUID());

		// SecurityContext: Manager로 설정
		UserDetailsImpl userDetails = new UserDetailsImpl(customerUser);
		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	// ==================== 답글 등록 ====================
	@Test
	void testCreateReply_manager() {
		ReqCreateReplyDto dto = new ReqCreateReplyDto();
		//dto.setContext("매니저 답글 내용");

		when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
		when(managerRepository.findByUserId(testManagerId)).thenReturn(Optional.of(testManager));
		when(ownerRepository.findByUserId(testManagerId)).thenReturn(Optional.empty());
		when(replyRepository.save(any(Reply.class))).thenAnswer(invocation -> {
			Reply r = invocation.getArgument(0);
			r.setId(UUID.randomUUID());
			return r;
		});

		ResViewReplyDto result = replyService.createReply(dto, testReview.getId(), testManagerId);
		System.out.println("result = " + result);

		assertNotNull(result);
		assertEquals(dto.getContext(), result.getContext());
		assertEquals("매니저닉네임", result.getWriterName());
	}

	@Test
	void testCreateReply_notStoreOwner_throwsException() {
		ReqCreateReplyDto dto = new ReqCreateReplyDto();
		dto.setContext("권한 없는 답글");

		// 리뷰의 가게 Owner는 다른 Owner
		Owner otherOwner = Owner.builder()
			.user(new User() {{
				setId(999L);
			}})
			.nickname("다른점주")
			.email("other@test.com")
			.phoneNumber("010-9999-8888")
			.businessNumber("987-65-43210")
			.build();
		otherOwner.setId(UUID.randomUUID());

		when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
		when(ownerRepository.findByUserId(testOwnerId)).thenReturn(Optional.of(otherOwner));
		when(managerRepository.findByUserId(testOwnerId)).thenReturn(Optional.empty());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> replyService.createReply(dto, testReview.getId(), testOwnerId));

		System.out.println("exception = " + exception.getMessage());

		assertEquals("해당 가게의 점주만 리뷰 답글을 등록할 수 있습니다.", exception.getMessage());
	}

	@Test
	void testCreateReply_customerCannotRegister_throwsException() {
		ReqCreateReplyDto dto = new ReqCreateReplyDto();
		dto.setContext("고객 답글");

		// 고객은 Owner도 Manager도 아님
		when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
		when(ownerRepository.findByUserId(testOwnerId)).thenReturn(Optional.empty());
		when(managerRepository.findByUserId(testManagerId)).thenReturn(Optional.empty());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> replyService.createReply(dto, testReview.getId(), testCustomerId));
		System.out.println("exception.getMessage() = " + exception.getMessage());

		assertEquals("리뷰 답글을 등록할 권한이 없습니다.", exception.getMessage());
	}

	// ==================== 답글 수정 ====================
	@Test
	void testUpdateReply_manager() {
		UUID replyId = UUID.randomUUID();
		ReqUpdateReplyDto dto = new ReqUpdateReplyDto();
		dto.setContext("매니저 수정 답글");

		Reply reply = Reply.builder()
			.context("원본 답글")
			.manager(testManager)
			.review(testReview)
			.build();
		reply.setId(replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));
		when(managerRepository.findByUserId(testManagerId)).thenReturn(Optional.of(testManager));
		when(ownerRepository.findByUserId(testManagerId)).thenReturn(Optional.empty());

		ResViewReplyDto result = replyService.updateReply(dto, replyId, testManagerId);
		System.out.println("result = " + result);

		assertEquals(dto.getContext(), result.getContext());
		assertEquals(reply.getContext(), result.getContext());
	}

	@Test
	void testUpdateReply_notStoreOwner_throwsException() {
		UUID replyId = UUID.randomUUID();
		ReqUpdateReplyDto dto = new ReqUpdateReplyDto();
		dto.setContext("권한 없는 수정");

		// 실제 가게 점주
		Owner storeOwner = testOwner;

		// 다른 사용자가 수정 시도
		Owner otherOwner = Owner.builder()
			.user(new User() {{
				setId(999L);
			}})
			.nickname("다른점주")
			.email("other@test.com")
			.phoneNumber("010-9999-8888")
			.businessNumber("987-65-43210")
			.build();
		otherOwner.setId(UUID.randomUUID());

		// Reply는 가게 점주가 작성
		Reply reply = Reply.builder()
			.context("원본 답글")
			.owner(storeOwner)  // 여기서 storeOwner
			.review(testReview)
			.build();
		reply.setId(replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));
		when(ownerRepository.findByUserId(otherOwner.getUser().getId())).thenReturn(Optional.of(otherOwner));
		when(managerRepository.findByUserId(otherOwner.getUser().getId())).thenReturn(Optional.empty());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> replyService.updateReply(dto, replyId, otherOwner.getUser().getId()));

		assertEquals("해당 가게의 점주만 리뷰 답글을 수정할 수 있습니다.", exception.getMessage());
	}

	@Test
	void testUpdateReply_customerCannotUpdate_throwsException() {
		UUID replyId = UUID.randomUUID();
		ReqUpdateReplyDto dto = new ReqUpdateReplyDto();
		dto.setContext("고객 수정");

		Reply reply = Reply.builder()
			.context("원본 답글")
			.review(testReview)
			.build();
		reply.setId(replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> replyService.updateReply(dto, replyId, testCustomerId));

		assertEquals("답글을 수정할 권한이 없습니다.", exception.getMessage());
	}

	// ==================== 답글 삭제 ====================
	@Test
	void testDeleteReply_manager() {
		UUID replyId = UUID.randomUUID();

		Reply reply = Reply.builder()
			.context("삭제할 답글")
			.manager(testManager)
			.review(testReview)
			.build();
		reply.setId(replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));
		when(managerRepository.findByUserId(testManagerId)).thenReturn(Optional.of(testManager));
		when(ownerRepository.findByUserId(testManagerId)).thenReturn(Optional.empty());

		ResDeleteReplyDto dto = replyService.deleteReply(replyId, testManagerId);
		System.out.println("dto = " + dto);

		assertNotNull(dto);
	}

	@Test
	void testDeleteReply_notStoreOwner_throwsException() {
		UUID replyId = UUID.randomUUID();

		// 실제 리뷰 작성자는 가게 점주
		Owner storeOwner = testOwner;

		// 권한 없는 다른 Owner
		Owner otherOwner = Owner.builder()
			.user(new User() {{
				setId(999L);
			}})
			.nickname("다른점주")
			.email("other@test.com")
			.phoneNumber("010-9999-8888")
			.businessNumber("987-65-43210")
			.build();
		otherOwner.setId(UUID.randomUUID());

		Reply reply = Reply.builder()
			.context("삭제할 답글")
			.owner(storeOwner)  // 가게 점주
			.review(testReview)
			.build();
		reply.setId(replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));
		when(ownerRepository.findByUserId(otherOwner.getUser().getId())).thenReturn(Optional.of(otherOwner));
		when(managerRepository.findByUserId(otherOwner.getUser().getId())).thenReturn(Optional.empty());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> replyService.deleteReply(replyId, otherOwner.getUser().getId()));

		assertEquals("해당 가게의 점주만 리뷰 답글을 삭제할 수 있습니다.", exception.getMessage());
	}

	@Test
	void testDeleteReply_customerCannotDelete_throwsException() {
		UUID replyId = UUID.randomUUID();

		Reply reply = Reply.builder()
			.context("삭제할 답글")
			.review(testReview)
			.build();
		reply.setId(replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> replyService.deleteReply(replyId, testCustomerId));

		assertEquals("답글을 삭제할 권한이 없습니다.", exception.getMessage());
	}*/
}