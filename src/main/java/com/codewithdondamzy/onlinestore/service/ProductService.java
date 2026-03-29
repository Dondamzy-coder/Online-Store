package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateProductRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.CreateProductResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.DeleteProductResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.GetProductResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.UpdateProductResponse;
import org.springframework.data.domain.Pageable;


public interface ProductService {
    CreateProductResponse createProduct(CreateProductRequest createProductRequest);

    GetProductResponse getAllProducts(Pageable pageable);

    GetProductResponse getProductById(Long id);

    UpdateProductResponse updateProductById(CreateProductRequest createProductRequest, Long id);

    DeleteProductResponse deleteProductById(Long id);

    GetProductResponse getProductByCategoryName(String category);

   GetProductResponse getProductByBrand(String brand);

   GetProductResponse getProductByCategoryNameAndBrand(String category,String brand);

    GetProductResponse getProductByName(String name);

   GetProductResponse getProductByBrandAndName(String brand, String name);

   UpdateProductResponse updateProductPrice(CreateProductRequest createProductRequest, Long id);

   GetProductResponse activateProduct(Long productId);

}
