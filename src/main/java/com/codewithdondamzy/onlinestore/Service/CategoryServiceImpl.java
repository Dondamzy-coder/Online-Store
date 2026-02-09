package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCategoryRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.CategoryResponse;

import com.codewithdondamzy.onlinestore.Models.Category;
import com.codewithdondamzy.onlinestore.Models.Products;
import com.codewithdondamzy.onlinestore.Repository.CategoryRepository;
import com.codewithdondamzy.onlinestore.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository, ProductRepository productRepository1) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository1;
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) {
        CategoryResponse categoryResponse = new CategoryResponse();
        try {
            List<Products> products = new ArrayList<>();
            Category category = Category.builder()
                    .name(createCategoryRequest.getName())
                    .productsList(products)
                    .build();

            categoryRepository.save(category);
            categoryResponse.setStatusCode(200);
            categoryResponse.setMessage("Category created");
            return categoryResponse;
        } catch (Exception e) {
            categoryResponse.setStatusCode(404);
            categoryResponse.setMessage("Category creation failed");
            return categoryResponse;
        }

    }

    @Override
    public CategoryResponse getCategoryByName(String name) {
        CategoryResponse categoryResponse = new CategoryResponse();
        try {
            Optional<Category> category = categoryRepository.findByName(name);
            if(category.isEmpty()){
                categoryResponse.setStatusCode(400);
                categoryResponse.setMessage("Category not found");
                return categoryResponse;
            }
            Category categoryToGet = category.get();
            categoryResponse.setStatusCode(200);
            categoryResponse.setMessage("Category found");
            categoryResponse.setData(categoryToGet);
            return categoryResponse;
        } catch (Exception e) {
            categoryResponse.setStatusCode(500);
            categoryResponse.setMessage("Invalid category name");
            return categoryResponse;
        }
    }

    @Override
    public CategoryResponse updateCategory(CreateCategoryRequest createCategoryRequest, String name) {
        CategoryResponse categoryResponse = new CategoryResponse();
        try {
            Optional<Category> category = categoryRepository.findByName(name);
            if(category.isEmpty()){
                categoryResponse.setStatusCode(404);
                categoryResponse.setMessage("Category not found");
                return categoryResponse;
            }
            Category categoryToUpdate = category.get();
            categoryToUpdate.setName(createCategoryRequest.getName());
            categoryRepository.save(categoryToUpdate);
            categoryResponse.setStatusCode(200);
            categoryResponse.setMessage("Category updated");
            return categoryResponse;
        } catch (Exception e) {
            categoryResponse.setStatusCode(404);
            categoryResponse.setMessage("Category update failed");
       return categoryResponse;
        }

    }

    @Override
    public CategoryResponse getAllCategories() {
        CategoryResponse categoryResponse = new CategoryResponse();
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty()){
            categoryResponse.setStatusCode(400);
            categoryResponse.setMessage("Categories not found");
            return categoryResponse;
        }
        List<Category> storeCategories = new ArrayList(categories);
        categoryResponse.setStatusCode(200);
        categoryResponse.setMessage("Categories found");
        categoryResponse.setData(storeCategories);
        return categoryResponse;
    }

    @Override
    public CategoryResponse deleteCategoryById(Long id) {
        CategoryResponse categoryResponse = new CategoryResponse();
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty()){
            categoryResponse.setStatusCode(404);
            categoryResponse.setMessage("Category not found");
            return categoryResponse;
        }
        productRepository.deleteAll(category.get().getProductsList());
        categoryRepository.delete(category.get());
        categoryResponse.setStatusCode(200);
        categoryResponse.setMessage("Category deleted successfully!");
        return categoryResponse;

    }

    @Transactional
    @Override
    public CategoryResponse deleteCategoryByName(String name) {
        CategoryResponse categoryResponse = new CategoryResponse();
        try {
            Optional<Category> category = categoryRepository.findByName(name);
            if(category.isEmpty()){
                categoryResponse.setStatusCode(404);
                categoryResponse.setMessage("Category not found");
                return categoryResponse;
            }
            productRepository.deleteAll(category.get().getProductsList());
            categoryRepository.delete(category.get());
            categoryResponse.setStatusCode(200);
            categoryResponse.setMessage("Category deleted successfully!");
            return categoryResponse;
        } catch (Exception e) {
            e.printStackTrace();
            categoryResponse.setStatusCode(500);
            categoryResponse.setMessage("Category delete failed");
        return categoryResponse;
        }
    }
}
