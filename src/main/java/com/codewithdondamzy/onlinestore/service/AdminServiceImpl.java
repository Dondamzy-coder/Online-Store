package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Request.AdminRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCategoryRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateProductRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.AdminResponse;
import com.codewithdondamzy.onlinestore.Models.Admin;
import com.codewithdondamzy.onlinestore.Models.Category;
import com.codewithdondamzy.onlinestore.Models.Products;
import com.codewithdondamzy.onlinestore.Repository.AdminRepository;
import com.codewithdondamzy.onlinestore.Repository.CategoryRepository;
import com.codewithdondamzy.onlinestore.Repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;


    @Override
    public AdminResponse createAdmin(AdminRequest adminRequest) {
        AdminResponse adminResponse = new AdminResponse();
        Optional<Admin> admin = adminRepository.findByEmailAddress(adminRequest.getEmail());
        if(admin.isPresent()) {
            adminResponse.setStatusCode(400);
            adminResponse.setMessage("Admin with email address " + adminRequest.getEmail() + " already exists");
            return adminResponse;
        }
        Admin newAdmin = Admin.builder()
                .userName(adminRequest.getUserName())
                .emailAddress(adminRequest.getEmail())
                .UUID(UUID.randomUUID().toString())
                .build();
        adminRepository.save(newAdmin);
        adminResponse.setStatusCode(200);
        adminResponse.setMessage("Admin created successfully");
        return adminResponse;
    }

    @Override
    public AdminResponse getAdminByUserName(String userName) {
        AdminResponse adminResponse = new AdminResponse();
       Optional<Admin> existingAdmin = adminRepository.findByUserName(userName);
       if(existingAdmin.isEmpty()) {
           adminResponse.setStatusCode(404);
           adminResponse.setMessage("Admin with username " + userName + " not found");
           return adminResponse;
       }
       Admin adminToGet = existingAdmin.get();
       adminResponse.setStatusCode(200);
       adminResponse.setMessage("Admin with username " + userName + " found successfully");
       adminResponse.setData(adminToGet);
       return adminResponse;
    }

    @Override
    public AdminResponse updateAdmin(AdminRequest adminRequest, String userName) {
        AdminResponse adminResponse = new AdminResponse();
        Optional<Admin> existingAdmin = adminRepository.findByUserName(userName);
        if(existingAdmin.isEmpty()) {
            adminResponse.setStatusCode(404);
            adminResponse.setMessage("Admin with username " + userName + " not found, Cannot be updated");
            return adminResponse;
        }
        Admin adminToUpdate = existingAdmin.get();
        adminToUpdate.setUserName(adminRequest.getUserName());
        adminToUpdate.setEmailAddress(adminRequest.getEmail());
        adminRepository.save(adminToUpdate);
        adminResponse.setStatusCode(200);
        adminResponse.setMessage("Admin with username " + userName + " updated successfully");
        return adminResponse;
    }

    @Override
    public AdminResponse getAllAdmins() {
        AdminResponse adminResponse = new AdminResponse();
        List<Admin> admins = adminRepository.findAll();
        if(admins.isEmpty()) {
            adminResponse.setStatusCode(400);
            adminResponse.setMessage("No admins found");
            return adminResponse;
        }
        adminResponse.setStatusCode(200);
        adminResponse.setMessage("Admins found successfully");
        adminResponse.setData(admins);
        return adminResponse;
    }

    @Override
    public AdminResponse deleteAdminByUserName(String userName) {
        Optional<Admin> existingAdmin = adminRepository.findByUserName(userName);
        if(existingAdmin.isEmpty()) {
            AdminResponse adminResponse = new AdminResponse();
            adminResponse.setStatusCode(404);
            adminResponse.setMessage("Admin with username " + userName + " not found, Cannot be deleted");
            return adminResponse;
        }
        adminRepository.delete(existingAdmin.get());
        AdminResponse adminResponse = new AdminResponse();
        adminResponse.setStatusCode(200);
        adminResponse.setMessage("Admin with username " + userName + " deleted successfully");
        return adminResponse;
    }

    @Override
    public AdminResponse updateCategory(Long id, CreateCategoryRequest createCategoryRequest) {
        AdminResponse adminResponse = new AdminResponse();
        try {

            Optional<Category> category = categoryRepository.findById(id);
            if(category.isEmpty()) {
                adminResponse.setStatusCode(404);
                adminResponse.setMessage("Category with id " + id + " not found, Cannot be updated");
                return adminResponse;
            }
            Category categoryToUpdate = category.get();
            categoryToUpdate.setName(createCategoryRequest.getName());
            categoryRepository.save(categoryToUpdate);
            adminResponse.setStatusCode(200);
            adminResponse.setMessage("Category with id " + id + " updated successfully");
            return adminResponse;
        } catch (Exception e) {
            adminResponse.setStatusCode(500);
            adminResponse.setMessage("Internal server error..Sorry there was a problem updating the category");
            return adminResponse;
        }
    }

    @Override
    public AdminResponse activateCategory(Long id) {
        AdminResponse adminResponse = new AdminResponse();
        try {
            Optional<Category> category = categoryRepository.findById(id);
            if(category.isEmpty()) {
                adminResponse.setStatusCode(404);
                adminResponse.setMessage("Category not found");
                return adminResponse;
            }
            Category catToActivate = category.get();
            if(catToActivate.isActive()) {
                adminResponse.setStatusCode(401);
                adminResponse.setMessage("Category is already activate cannot be activated again");
                return adminResponse;
            }
            catToActivate.setActive(true);
            categoryRepository.save(catToActivate);
            adminResponse.setStatusCode(200);
            adminResponse.setMessage("Category is activated successfully");
            return adminResponse;
        } catch (Exception e) {
            adminResponse.setStatusCode(500);
            adminResponse.setMessage("There was A problem activating the category");
            return adminResponse;
        }
    }

    @Override
    public AdminResponse deactivateCategory(Long id) {
        AdminResponse adminResponse = new AdminResponse();
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty()) {
            adminResponse.setStatusCode(404);
            adminResponse.setMessage("Category not found");
            return adminResponse;
        }
        Category catToDeactivate = category.get();
        catToDeactivate.setActive(false);
        categoryRepository.save(catToDeactivate);
        adminResponse.setStatusCode(200);
        adminResponse.setMessage("Category is deactivated successfully");
        return adminResponse;
    }

    @Override
    public AdminResponse updateProduct(Long productId,Long categoryId, CreateProductRequest createProductRequest,int quantity) {
        AdminResponse adminResponse = new AdminResponse();
        try {
            Optional<Category> category = categoryRepository.findById(categoryId);
            Optional<Products> product = productRepository.findProductsById(productId);
            if(product.isEmpty()) {
                adminResponse.setStatusCode(404);
                adminResponse.setMessage("Product with id " + productId + " not found, Cannot be updated");
                return adminResponse;
            }
            if(category.isEmpty()) {
                adminResponse.setStatusCode(402);
                adminResponse.setMessage("Category not found,product cannot be updated");
                return adminResponse;
            }
            if(product.get().getInventory() < 0) {
                adminResponse.setStatusCode(401);
                adminResponse.setMessage("Product inventory is negative");
                return adminResponse;
            }
            Category categoryToUpdate = category.get();
            Products productToUpdate = product.get();
            productToUpdate.setInventory(createProductRequest.getStockQuantity() + quantity);
            productToUpdate.setPrice(createProductRequest.getPrice());
            productToUpdate.setDescription(createProductRequest.getDescription());
            productToUpdate.setCategory(categoryToUpdate);
            productRepository.save(productToUpdate);
            adminResponse.setStatusCode(200);
            adminResponse.setMessage("Product updated successfully");
            return adminResponse;
        } catch (Exception e) {
            adminResponse.setStatusCode(500);
            adminResponse.setMessage("There was a problem updating the product");
            return adminResponse;
        }
    }

    @Override
    public AdminResponse deleteProduct(Long id) {
        AdminResponse adminResponse = new AdminResponse();
        Optional<Products> product = productRepository.findProductsById(id);
        if(product.isEmpty()) {
            adminResponse.setStatusCode(404);
            adminResponse.setMessage("Product with id " + id + " not found, Cannot be deleted");
        }
        Products productToDelete = product.get();
        productRepository.delete(productToDelete);
        adminResponse.setStatusCode(200);
        adminResponse.setMessage("Product deleted successfully");
        adminResponse.setData(productToDelete);
        return adminResponse;
    }

    @Override
    public AdminResponse addProductToCategory(Long id,Long categoryId, CreateProductRequest createProductRequest) {
        AdminResponse adminResponse = new AdminResponse();
        Optional<Products> product = productRepository.findProductsById(id);
        Optional<Category> category = categoryRepository.findById(categoryId);
        if(product.isEmpty()) {
            adminResponse.setStatusCode(404);
            adminResponse.setMessage("Product with id " + id + " not found, Cannot be added");
            return adminResponse;
        }
        if(category.isEmpty()) {
            adminResponse.setStatusCode(401);
            adminResponse.setMessage("Category not found");
            return adminResponse;
        }
        Products productToAdd = product.get();
        Category categoryToAdd = category.get();
        productToAdd.setCategory(categoryToAdd);
        productRepository.save(productToAdd);
        adminResponse.setStatusCode(200);
        adminResponse.setMessage("Product added to category successfully!");
        return adminResponse;
    }

    @Override
    public AdminResponse updateProductPrice(Long id, CreateProductRequest createProductRequest) {
        AdminResponse adminResponse = new AdminResponse();
        try {
            Optional<Products> products = productRepository.findProductsById(id);
            if(products.isEmpty()) {
                adminResponse.setStatusCode(401);
                adminResponse.setMessage("Product not found,price cannot be update");
                return adminResponse;
            }
            Products productToUpdate = products.get();
            if(!productToUpdate.isAvailable()) {
                adminResponse.setStatusCode(404);
                adminResponse.setMessage("Product not available in the store");
                return adminResponse;
            }
            productToUpdate.setPrice(createProductRequest.getPrice());
            productRepository.save(productToUpdate);
            adminResponse.setStatusCode(200);
            adminResponse.setMessage("Product price updated successfully");
            return adminResponse;
        } catch (Exception e) {
            adminResponse.setStatusCode(500);
            adminResponse.setMessage("Sorry, there was a problem updating the product price");
            return adminResponse;
        }
    }

    @Override
    public AdminResponse increaseStock(Long id,int quantity) {
        AdminResponse adminResponse = new AdminResponse();
        try {
            if(quantity <= 0) {
                adminResponse.setStatusCode(400);
                adminResponse.setMessage("quantity must be greater than 0");
                return adminResponse;
            }
            Optional<Products> product = productRepository.findProductsById(id);
            if(product.isEmpty()) {
                adminResponse.setStatusCode(404);
                adminResponse.setMessage("Product not found,product stock quantity cannot be increased");
                return adminResponse;
            }
            Products productToIncrease = product.get();
            if(productToIncrease.getInventory() < 0) {
                adminResponse.setStatusCode(401);
                adminResponse.setMessage("This product is no longer available in stock");
                return adminResponse;
            }
            int newInventory = productToIncrease.getInventory() + quantity;
            productToIncrease.setInventory(newInventory);
            adminResponse.setStatusCode(200);
            adminResponse.setMessage("Product stock increased successfully");
            return adminResponse;
        } catch (Exception e) {
            adminResponse.setStatusCode(500);
            adminResponse.setMessage("Sorry, there was a problem increasing the product stock quantity");
            return adminResponse;
        }
    }

    @Override
    public AdminResponse decreaseStock(Long id,int quantity) {
            AdminResponse adminResponse = new AdminResponse();
        try {
            if(quantity <= 0) {
                adminResponse.setStatusCode(400);
                adminResponse.setMessage("quantity must be greater than 0");
                return adminResponse;
            }
            Optional<Products> product = productRepository.findProductsById(id);
            if(product.isEmpty()) {
                adminResponse.setStatusCode(404);
                adminResponse.setMessage("Product not found,product stock quantity cannot be decreased");
                return adminResponse;
            }
            Products productToDec = product.get();
            if(productToDec.getInventory() < 0) {
                adminResponse.setStatusCode(401);
                adminResponse.setMessage("This product is no longer available in stock");
                return adminResponse;
            }
            int newInventory = productToDec.getInventory() - quantity;
            productToDec.setInventory(newInventory);
            adminResponse.setStatusCode(200);
            adminResponse.setMessage("Product stock decreased successfully");
            return adminResponse;
        } catch (Exception e) {
            adminResponse.setStatusCode(500);
            adminResponse.setMessage("Sorry, there was a problem decreasing the product stock quantity");
            return adminResponse;
        }
    }
}
