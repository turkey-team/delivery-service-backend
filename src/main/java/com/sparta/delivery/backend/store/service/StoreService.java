package com.sparta.delivery.backend.store.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.sparta.delivery.backend.category.entity.Category;
import com.sparta.delivery.backend.category.repository.CategoryRepository;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.owner.repository.OwnerRepository;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.store.dto.ReqCreateStoreDto;
import com.sparta.delivery.backend.store.dto.ReqUpdateStoreDetailsDto;
import com.sparta.delivery.backend.store.dto.ReqUpdateStoreInfoDto;
import com.sparta.delivery.backend.store.dto.ResCreateStoreDto;
import com.sparta.delivery.backend.store.dto.ResGetStoreDto;
import com.sparta.delivery.backend.store.dto.ResUpdateStoreDetailsDto;
import com.sparta.delivery.backend.store.dto.ResUpdateStoreInfoDto;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreCategory;
import com.sparta.delivery.backend.store.entity.StoreDetails;
import com.sparta.delivery.backend.store.entity.StoreImage;
import com.sparta.delivery.backend.store.entity.StoreImageStatusEnum;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;
import com.sparta.delivery.backend.store.repository.StoreCategoryRepository;
import com.sparta.delivery.backend.store.repository.StoreDetailsRepository;
import com.sparta.delivery.backend.store.repository.StoreImageRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final OwnerRepository ownerRepository;
	private final ImageRepository imageRepository;
	private final CategoryRepository categoryRepository;
	private final DongRepository dongRepository;
	private final StoreDetailsRepository storeDetailsRepository;
	private final StoreCategoryRepository storeCategoryRepository;
	private final StoreImageRepository storeImageRepository;

	@Transactional
	public ResCreateStoreDto createStore(ReqCreateStoreDto requestDto , User user) {

		// 1. Owner 검증

		if (user.getRole() != UserRoleEnum.OWNER){
			throw new AccessDeniedException("가게 생성 권한이 없습니다");
		}

		//Owner 조회
		Owner owner = ownerRepository.findByUser_PublicId(user.getPublicId());

		// Region Dong 조회
		Dong dong = dongRepository.findByCode(requestDto.getRegionCode());

		// 카테고리 조회

		//Category
		List<Category> categories = categoryRepository.findAllById(requestDto.getCategories());

		List<StoreCategory> storeCategories = new ArrayList<>();

		// 2. 이미지 등록
		List<String> storeImageUrls = requestDto.getImages().stream()
			.filter(img -> "store".equalsIgnoreCase(img.getType()))
			.map(ReqCreateStoreDto.ImageDto::getUrl)
			.collect(Collectors.toList());

		List<String> businessImageUrls = requestDto.getImages().stream()
			.filter(img -> "business".equalsIgnoreCase(img.getType()))
			.map(ReqCreateStoreDto.ImageDto::getUrl)
			.collect(Collectors.toList());

		// 2-1.image table insert
		List<Image> storeImages = saveImages(storeImageUrls);
		List<Image> businessImage = saveImages(businessImageUrls);

		// 3. store insert

		// store 생성
		Store store = Store.builder()
			.owner(owner)
			.name(requestDto.getName())
			.regionDong(dong)
			.addressDetails(requestDto.getAddressDetail())
			.deliveryFee(requestDto.getDeliveryFee())
			.minOrderPrice(requestDto.getMinOrderPrice())
			.reviewRate(5.0)
			.phoneNumber(requestDto.getPhoneNumber())
			.status(StoreStatusEnum.CLOSED)
			.build();

		// save
		storeRepository.save(store);

		// 3-2 카테고리 중간 테이블에 저장
		for(Category category : categories){
			StoreCategory storeCategory = StoreCategory.builder().category(category).store(store).build();
			storeCategories.add(storeCategory);
			category.getStoreCategories().add(storeCategory);
			store.getStoreCategories().add(storeCategory);
		}

		storeCategoryRepository.saveAll(storeCategories);

		// 3-2. 이미지 중간 테이블에 저장
		//매장

		saveStoreImages(storeImages, store, StoreImageStatusEnum.STORE);
		saveStoreImages(businessImage, store, StoreImageStatusEnum.BUSINESS);


		// 4. store_details insert
		StoreDetails storeDetails = StoreDetails.builder().store(store)
			.operationHours(requestDto.getOperatingHours())
			.businessNumber(requestDto.getBusinessNumber())
			.holiday(requestDto.getHoliday())
			.description(requestDto.getDescription()).build();

		storeDetailsRepository.save(storeDetails);


		return new ResCreateStoreDto(store.getId(), store.getName());
	}

	public ResGetStoreDto getStore(UUID storeId) {

		Store store = storeRepository.findById(storeId).orElseThrow(()->new IllegalArgumentException("해당 가게가 존재하지 않습니다"));
		StoreDetails details = storeDetailsRepository.findByStoreId(storeId).orElseThrow(()->new IllegalArgumentException("해당 가게가 존재하지 않습니다"));


		StoreImage storeImage = storeImageRepository.findFirstByStoreIdAndStatusOrderByCreatedAtAsc(storeId, StoreImageStatusEnum.STORE);

		ResGetStoreDto resGetStoreDto = ResGetStoreDto.builder()
			.storeid(store.getId())
			.name(store.getName())
			.reviewRate(store.getReviewRate())
			.reviewCnt(store.getReviewCnt())
			.status(store.getStatus())
			.deliveryFee(store.getDeliveryFee())
			.minOrderPrice(store.getMinOrderPrice())
			.description(details.getDescription())
			.holiday(details.getHoliday())
			.operationHours(details.getOperationHours())
			.imageUrl(storeImage.getImage().getImageUrl())
			.build();

		return resGetStoreDto;
	}



	/**
	 * Image 테이블 저장
	 * @param urls
	 * @return
	 */
	public List<Image> saveImages(List<String> urls) {

		if (urls == null || urls.isEmpty()) return Collections.emptyList();

		List<Image> images = new ArrayList<>();

		for (String url : urls) {
			Image image = Image.builder().imageUrl(url).build();
			imageRepository.save(image);
			images.add(image);
		}

		return images;
	}

	/**
	 * StoreImage 테이블 저장
	 * @param images
	 * @param store
	 * @param status STORE, BUSINESS
	 */
	public void saveStoreImages(List<Image> images, Store store, StoreImageStatusEnum status) {

		for(Image image : images){
			StoreImage storeImage = store.addImage(store, image, status);
			image.getStoreImages().add(storeImage);
			store.getStoreImages().add(storeImage);
			storeImageRepository.save(storeImage);
		}

	}

}

