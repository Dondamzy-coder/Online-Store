package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCategoryRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.CategoryResponse;
import org.springframework.data.domain.Pageable;


public interface CategoryService {
    CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);
    CategoryResponse getCategoryByName(String name);
    CategoryResponse updateCategory(CreateCategoryRequest createCategoryRequest,String name);
    CategoryResponse getAllCategories(Pageable pageable);
    CategoryResponse deleteCategoryById(Long id);
    CategoryResponse deleteCategoryByName(String name);
}
