package com.sparta.delivery.backend.category.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.sparta.delivery.backend.category.dto.ResCreateCategoryDto;
import com.sparta.delivery.backend.category.dto.ResDeleteCategoryDto;
import com.sparta.delivery.backend.category.dto.ResGetCategoryDto;
import com.sparta.delivery.backend.category.dto.ResGetListCategoryDto;
import com.sparta.delivery.backend.category.dto.ResUpdateCategoryDto;
import com.sparta.delivery.backend.category.entity.Category;
import com.sparta.delivery.backend.category.repository.CategoryRepository;
import com.sparta.delivery.backend.global.common.dto.PageResponse;
import com.sparta.delivery.backend.store.repository.StoreCategoryRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
	@InjectMocks
	CategoryService categoryService;

	@Mock
	CategoryRepository categoryRepository;

	@Mock
	private StoreCategoryRepository storeCategoryRepository;

	private User mockManagerUser;
	private User mockCustomerUser;

	@BeforeEach
	void setUp() {
		mockManagerUser = mock(User.class);
		//when(mockManagerUser.getRole()).thenReturn(UserRoleEnum.MANAGER);
		//when(mockManagerUser.getId()).thenReturn(1L);

		mockCustomerUser = mock(User.class);
		//when(mockCustomerUser.getRole()).thenReturn(UserRoleEnum.CUSTOMER);
	}

	@Test
	@DisplayName("카테고리 생성 : 성공")
	void createCategory_Success() {
		// given
		String categoryName = "한식";
		when(categoryRepository.existsByName(categoryName)).thenReturn(false);

		Category savedCategory = Category.builder().name(categoryName).build();
		when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

		// when
		ResCreateCategoryDto result = categoryService.createCategory(categoryName, mockManagerUser);

		// then
		assertEquals(categoryName, result.getName());
		verify(categoryRepository).save(any(Category.class));
	}

	@Test
	@DisplayName("카테고리 생성 : 중복된 이름으로 실패")
	void createCategory_Fail_DuplicateName() {
		// given
		String categoryName = "중복된카테고리";
		when(categoryRepository.existsByName(categoryName)).thenReturn(true);

		// when & then
		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
			() -> categoryService.createCategory(categoryName, mockManagerUser));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
	}

	@Test
	@DisplayName("카테고리 수정 : 성공")
	void updateCategoryTest_Success() {
		// given
		UUID categoryId = UUID.randomUUID();
		String oldName = "한식";
		String newName = "양식";

		Category category = Category.builder().name(oldName).build();
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
		when(categoryRepository.save(any(Category.class))).thenReturn(category);

		// when
		ResUpdateCategoryDto result = categoryService.updateCategory(categoryId, newName, mockManagerUser);

		// then
		assertEquals(newName, result.getName());
		verify(categoryRepository).save(any(Category.class));
	}

	@Test
	@DisplayName("카테고리 수정 : 존재하지 않는 카테고리 실패")
	void updateCategoryTest_Fail_NotFound() {
		// given
		UUID categoryId = UUID.randomUUID();
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		// when & then
		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
			() -> categoryService.updateCategory(categoryId, "한식", mockManagerUser));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("해당 카테고리가 존재하지 않습니다.", exception.getReason());
	}

	@Test
	@DisplayName("카테고리 삭제 : 성공")
	void deleteCategoryTest_Success() {
		// given
		UUID categoryId = UUID.randomUUID();
		Category category = Category.builder().name("디저트").build();
		ReflectionTestUtils.setField(category, "id", categoryId);

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
		when(storeCategoryRepository.existsByCategoryIdAndDeletedAtIsNull(categoryId)).thenReturn(false);
		when(categoryRepository.save(any())).thenReturn(category);

		// when
		ResDeleteCategoryDto result = categoryService.deleteCategory(categoryId, mockManagerUser);

		// then
		assertEquals(categoryId, result.getCategoryId());
		verify(categoryRepository).save(category);
	}

	@Test
	@DisplayName("카테고리 삭제 : 사용중인 카테고리는 삭제 불가")
	void deleteCategoryTest_Fail_InUse() {
		// given
		UUID categoryId = UUID.randomUUID();
		Category category = Category.builder().name("디저트").build();
		ReflectionTestUtils.setField(category, "id", categoryId);

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
		when(storeCategoryRepository.existsByCategoryIdAndDeletedAtIsNull(categoryId)).thenReturn(true);

		// when & then
		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
			() -> categoryService.deleteCategory(categoryId, mockManagerUser));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("현재 사용중인 카테고리는 삭제할 수 없습니다.", exception.getReason());
	}

	@Test
	@DisplayName("카테고리 상세조회 : 성공")
	void getCategoryTest_Success() {
		UUID categoryId = UUID.randomUUID();
		Category category = Category.builder().name("분식").build();
		ReflectionTestUtils.setField(category, "id", categoryId);

		when(categoryRepository.existsById(categoryId)).thenReturn(true);
		when(categoryRepository.findByIdAndDeletedAtIsNull(categoryId)).thenReturn(Optional.of(category));

		ResGetCategoryDto result = categoryService.getCategory(categoryId);

		assertEquals("분식", result.getCategoryName());
	}

	@Test
	@DisplayName("카테고리 상세조회 : 삭제된 카테고리 조회 실패")
	void getCategory_Deleted_Fail() {
		UUID categoryId = UUID.randomUUID();

		when(categoryRepository.existsById(categoryId)).thenReturn(true);
		when(categoryRepository.findByIdAndDeletedAtIsNull(categoryId)).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
			() -> categoryService.getCategory(categoryId));

		assertEquals("이미 삭제된 카테고리입니다.",exception.getReason());
	}

	@Test
	@DisplayName("카테고리 리스트 조회 : 키워드 검색 성공")
	void getCategories_WithKeyword_Success() {
		// given
		String keyword = "디저트";
		int page = 1;
		int size = 10;
		String sort = "name";

		Category dessert = Category.builder().name("디저트").build();
		ReflectionTestUtils.setField(dessert, "id", UUID.randomUUID());
		Category dessertTwo = Category.builder().name("디저트2").build();
		ReflectionTestUtils.setField(dessertTwo, "id", UUID.randomUUID());

		List<Category> categoryList = List.of(
			dessert,dessertTwo
		);


		Page<Category> pageResult = new PageImpl<>(categoryList);
		when(categoryRepository.findAllByNameContainingAndDeletedAtIsNull(eq(keyword), any(Pageable.class)))
			.thenReturn(pageResult);

		// when
		PageResponse<ResGetListCategoryDto> result = categoryService.getCategories(mockManagerUser, keyword, page, size, sort);

		// then
		assertEquals(2, result.getContent().size());
		assertTrue(result.getContent().get(0).getCategoryName().contains("디저트"));
	}

	@Test
	@DisplayName("카테고리 리스트 조회 : 키워드 결과 없음")
	void getCategories_KeywordNoResult() {
		// given
		String keyword = "없는카테고리";
		int page = 1;
		int size = 10;
		String sort = "name";

		Page<Category> emptyPage = Page.empty();
		when(categoryRepository.findAllByNameContainingAndDeletedAtIsNull(eq(keyword), any(Pageable.class)))
			.thenReturn(emptyPage);

		// when
		PageResponse<ResGetListCategoryDto> result = categoryService.getCategories(mockManagerUser, keyword, page, size, sort);

		// then
		assertTrue(result.getContent().isEmpty());
	}

}
