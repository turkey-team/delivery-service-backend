package com.sparta.delivery.backend.category.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.category.entity.Category;
import com.sparta.delivery.backend.category.service.CategoryService;

@RestController
@RequestMapping("/v1")
public class CategoryController {

	// @PostMapping("/category")
	// public Category createCategory(@RequestBody String name, @AuthenticationPrincipa UserDetailImpl userDetails) {
	// 	return CategoryService.createCategory(name, userDetails.getUser());
	// }
}
