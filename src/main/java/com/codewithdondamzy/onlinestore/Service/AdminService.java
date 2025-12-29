package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.AdminRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCategoryRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateProductRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.AdminResponse;

public interface AdminService {
    AdminResponse createAdmin(AdminRequest adminRequest);

    AdminResponse getAdminByUserName(String userName);

    AdminResponse updateAdmin(AdminRequest adminRequest,String userName);

    AdminResponse getAllAdmins();

    AdminResponse deleteAdminByUserName(String userName);

    AdminResponse updateCategory(Long id, CreateCategoryRequest createCategoryRequest);

    AdminResponse activateCategory(Long id);

    AdminResponse deactivateCategory(Long id);

    AdminResponse updateProduct(Long id, CreateProductRequest createProductRequest,int quantity);

    AdminResponse deleteProduct(Long id);

    AdminResponse addProductToCategory(Long id, Long categoryId, CreateProductRequest createProductRequest);

    AdminResponse updateProductPrice(Long id, CreateProductRequest createProductRequest);

    AdminResponse increaseStock(Long id,int quantity);

    AdminResponse decreaseStock(Long id,int quantity);

    
}
