package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCategoryRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.CategoryResponse;
import org.springframework.stereotype.Service;


public interface CategoryService {
    CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);
    CategoryResponse getCategoryByName(String name);
    CategoryResponse updateCategory(CreateCategoryRequest createCategoryRequest,String name);
    CategoryResponse getAllCategories();
    CategoryResponse deleteCategoryById(Long id);
    CategoryResponse deleteCategoryByName(String name);
}
