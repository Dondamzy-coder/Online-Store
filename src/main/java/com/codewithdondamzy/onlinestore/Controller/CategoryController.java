package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCategoryRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.CategoryResponse;
import com.codewithdondamzy.onlinestore.Service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/OnlineStore")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @PostMapping("/createCategory")
    public CategoryResponse createCategory(@RequestBody CreateCategoryRequest createCategoryRequest) {
        return categoryService.createCategory(createCategoryRequest);
    }
    @GetMapping("/getCategoryByName")
    public CategoryResponse getCategoryByName(@RequestParam String name) {
        return categoryService.getCategoryByName(name);
    }
    @GetMapping("/getAllCategories")
    public CategoryResponse getAllCategories() {
        return categoryService.getAllCategories();
    }
    @PutMapping("/updateCategory")
    public CategoryResponse updateCategory(@RequestBody CreateCategoryRequest createCategoryRequest,@RequestParam String name) {
        return categoryService.updateCategory(createCategoryRequest,name);
    }
    @DeleteMapping("/deleteCategoryByName")
    public CategoryResponse deleteCategoryByName(@RequestParam String name) {
        return categoryService.deleteCategoryByName(name);
    }

    @DeleteMapping("/deleteCategoryById/{id}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok().body(categoryService.deleteCategoryById(id));
    }
}
