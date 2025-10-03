package com.sparta.delivery.backend.reply.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReplyServiceTest {

	/*@InjectMocks
	private ReplyService replyService;

	@Mock
	private ReplyRepository replyRepository;

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private ReviewRepository reviewRepository;

	private User testUser;
	private Owner testOwner;
	private Review testReview;

	private Long testUserId = 1L;

	@BeforeEach
	void setUp() {
		// ==================== User / Owner / Review 객체 생성 ====================
		testUser = new User();
		testUser.setId(testUserId);
		testUser.setUsername("testOwner");
		testUser.setRole(UserRoleEnum.OWNER);

		testOwner = Owner.builder()
			.user(testUser)
			.nickname("테스트닉네임")
			.email("owner@test.com")
			.phoneNumber("010-1111-2222")
			.businessNumber("123-45-67890")
			.build();

		// 반드시 id 세팅
		testOwner.setId(UUID.randomUUID());

		testReview = Review.builder()
			.customer(null) // 단순 테스트용
			.store(null)
			.context("리뷰 내용")
			.rate(5)
			.build();
		testReview.setId(UUID.randomUUID());

		// ==================== SecurityContext 설정 ====================
		UserDetailsImpl userDetails = new UserDetailsImpl(testUser);
		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	// ==================== 답글 등록 ====================
	@Test
	void testCreateReply_authenticatedUser() {
		ReqCreateReplyDto dto = new ReqCreateReplyDto();
		dto.setContext("답글 내용");

		// Reply 객체 생성
		Reply reply = Reply.builder()
			.context(dto.getContext())
			.owner(testOwner)
			.review(testReview)
			.build();

		when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
		when(ownerRepository.findByUserId(testUserId)).thenReturn(Optional.of(testOwner));
		when(replyRepository.save(any(Reply.class))).thenAnswer(invocation -> {
			Reply r = invocation.getArgument(0);
			r.setId(UUID.randomUUID());          // id 자동 세팅
			return r;
		});

		ResViewReplyDto result = replyService.createReply(dto, testReview.getId());
		System.out.println("result = " + result);

		assertNotNull(result);
		assertEquals(dto.getContext(), result.getContext());
	}

	// ==================== 답글 수정 ====================
	@Test
	void testUpdateReply_authenticatedUser() {
		UUID replyId = UUID.randomUUID();
		ReqUpdateReplyDto dto = new ReqUpdateReplyDto();
		dto.setContext("수정 답글");

		Reply reply = Reply.builder()
			.context("원본 답글")
			.owner(testOwner)
			.review(testReview)
			.build();
		reply.setId(replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));
		when(ownerRepository.findByUserId(testUserId)).thenReturn(Optional.of(testOwner));

		ResViewReplyDto result = replyService.updateReply(dto, replyId);
		System.out.println("result = " + result);

		assertEquals(dto.getContext(), result.getContext());
		assertEquals(reply.getContext(), result.getContext());
	}

	// ==================== 답글 삭제 ====================
	@Test
	void testDeleteReply_authenticatedUser() {
		UUID replyId = UUID.randomUUID();

		Reply reply = Reply.builder()
			//.id(replyId)
			.context("삭제할 답글")
			.owner(testOwner)
			.review(testReview)
			.build();
		reply.setId(replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));
		when(ownerRepository.findByUserId(testUserId)).thenReturn(Optional.of(testOwner));

		ResViewReplyDto dto = replyService.deleteReply(replyId);

		assertNotNull(dto);
	}

	// ==================== 권한 없는 사용자 예외 ====================
	@Test
	void testUpdateReply_notAuthor_throwsException() {
		UUID replyId = UUID.randomUUID();
		ReqUpdateReplyDto dto = new ReqUpdateReplyDto();
		dto.setContext("수정 답글");

		Owner otherOwner = Owner.builder()
			.user(new User() {{
				setId(999L);
			}})
			.nickname("다른닉네임")
			.email("other@test.com")
			.phoneNumber("010-9999-8888")
			.businessNumber("987-65-43210")
			.build();
		otherOwner.setId(UUID.randomUUID());

		Reply reply = Reply.builder()
			//.id(replyId)
			.context("원본 답글")
			.owner(otherOwner)
			.review(testReview)
			.build();
		reply.setId(replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));
		when(ownerRepository.findByUserId(testUserId)).thenReturn(Optional.of(testOwner));

		assertThrows(UnauthorizedException.class, () -> replyService.updateReply(dto, replyId));
	}

	@Test
	void testDeleteReply_notAuthor_throwsException() {
		UUID replyId = UUID.randomUUID();

		Owner otherOwner = Owner.builder()
			.user(new User() {{
				setId(999L);
			}})
			.nickname("다른닉네임")
			.email("other@test.com")
			.phoneNumber("010-9999-8888")
			.businessNumber("987-65-43210")
			.build();
		otherOwner.setId(UUID.randomUUID());

		Reply reply = Reply.builder()
			//.id(replyId)
			.context("삭제할 답글")
			.owner(otherOwner)
			.review(testReview)
			.build();
		reply.setId(replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));
		when(ownerRepository.findByUserId(testUserId)).thenReturn(Optional.of(testOwner));

		assertThrows(UnauthorizedException.class, () -> replyService.deleteReply(replyId));
	}*/
}

