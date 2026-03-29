package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateProductRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.CreateProductResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.DeleteProductResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.GetProductResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.UpdateProductResponse;
import com.codewithdondamzy.onlinestore.Models.Category;
import com.codewithdondamzy.onlinestore.Models.Products;
import com.codewithdondamzy.onlinestore.Repository.CategoryRepository;
import com.codewithdondamzy.onlinestore.Repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository,CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public CreateProductResponse createProduct(CreateProductRequest createProductRequest) {
        CreateProductResponse createProductResponse = new CreateProductResponse();
        try {
            Category category = Category.builder()
                    .name(createProductRequest.getCategory().getName())
                    .isActive(true)
                    .productsList(new ArrayList<>())
                    .build();
            Products product = Products.builder()
                    .name(createProductRequest.getName())
                    .brand(createProductRequest.getBrand())
                    .description(createProductRequest.getDescription())
                    .price(createProductRequest.getPrice())
                    .category(category)
                    .isAvailable(true)
                    .cartItem(new ArrayList<>())
                    .reviews(new ArrayList<>())
                    .inventory(createProductRequest.getStockQuantity())
                    .imageList(new ArrayList<>())
                    .build();
            category.getProductsList().add(product);
            categoryRepository.save(category);
//            product.setName(createProductRequest.getName());
//            product.setPrice(createProductRequest.getPrice());
//            product.setDescription(createProductRequest.getDescription());
            productRepository.save(product);
            createProductResponse.setStatusCode(200);
            createProductResponse.setMessage("Product created successfully!!");
            createProductResponse.setProduct(product);
            return createProductResponse;
        } catch (Exception e) {
            e.printStackTrace();
            createProductResponse.setStatusCode(400);
            createProductResponse.setMessage("Invalid input.Product cannot be created");
        }
        return createProductResponse;
    }

    @Override
    public GetProductResponse getAllProducts(Pageable pageable) {
        GetProductResponse getProductResponse = new GetProductResponse();
        try {
            Page<Products> productsPage = productRepository.findAll(pageable);
            List<Products> productsList = productsPage.getContent();
            if(productsList.isEmpty()) {
                getProductResponse.setStatusCode(400);
                getProductResponse.setMessage("Products not available");
                return getProductResponse;
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", productsList);
            responseData.put("currentPage", productsPage.getNumber());
            responseData.put("totalPages", productsPage.getTotalPages());
            responseData.put("totalElements", productsPage.getTotalElements());
            responseData.put("numberOfElements", productsPage.getNumberOfElements());
            getProductResponse.setStatusCode(200);
            getProductResponse.setMessage("Products found successfully");
            getProductResponse.setData(responseData);
            return getProductResponse;
        } catch (Exception e) {
            e.printStackTrace();
            getProductResponse.setStatusCode(500);
            getProductResponse.setMessage("Invalid search!!");
            return getProductResponse;
        }
    }

    @Override
    public GetProductResponse getProductById(Long id) {
        GetProductResponse getProductResponse = new GetProductResponse();
        try {
            Optional<Products> existingProduct = productRepository.findProductsById(id);
            if(existingProduct.isEmpty()) {
                getProductResponse.setStatusCode(400);
                getProductResponse.setMessage("Product not available in store!");
                return getProductResponse;
            }
            Products productsToGet = existingProduct.get();
            getProductResponse.setStatusCode(200);
            getProductResponse.setMessage("Product found successfully");
            getProductResponse.setData(productsToGet);
            return getProductResponse;
        } catch (Exception e) {
            getProductResponse.setStatusCode(500);
            getProductResponse.setMessage("Invalid search!!");
        }
        return getProductResponse;
    }

    @Override
    public UpdateProductResponse updateProductById(CreateProductRequest createProductRequest, Long id) {
        UpdateProductResponse updateProductResponse = new UpdateProductResponse();
        try {
            Optional<Category> category = categoryRepository.findByName(createProductRequest.getCategory().getName());
            Optional<Products> products = productRepository.findProductsById(id);
            if(products.isEmpty()) {
                updateProductResponse.setStatusCode(400);
                updateProductResponse.setMessage("Product not found");
                return updateProductResponse;
            }
            if(category.isEmpty()) {
                updateProductResponse.setStatusCode(400);
                updateProductResponse.setMessage("Category not found");
                return updateProductResponse;
            }
            Category categoryToUpdate = category.get();
            Products productToUpDate = Products.builder()
                    .name(createProductRequest.getName())
                    .brand(createProductRequest.getBrand())
                    .price(createProductRequest.getPrice())
                    .description(createProductRequest.getDescription())
                    .category(categoryToUpdate)
                    .inventory(createProductRequest.getStockQuantity())
                    .isAvailable(true)
                    .build();
//            products.get();
//            productToUpDate.setName(createProductRequest.getName());
//            productToUpDate.setPrice(createProductRequest.getPrice());
//            productToUpDate.setDescription(createProductRequest.getDescription());
//            productToUpDate.setCategory(createProductRequest.getCategory());
            productRepository.save(productToUpDate);
            updateProductResponse.setStatusCode(200);
            updateProductResponse.setMessage("Product Updated successfully!!");
            return updateProductResponse;
        } catch (Exception e) {
            updateProductResponse.setStatusCode(404);
            updateProductResponse.setMessage("Invalid input");
        }
        return updateProductResponse;
    }

    @Override
    public DeleteProductResponse deleteProductById(Long id) {
        DeleteProductResponse deleteProductResponse = new DeleteProductResponse();
        try {
            Optional<Products> products = productRepository.findProductsById(id);
            if(products.isEmpty()) {
                deleteProductResponse.setStatusCode(400);
                deleteProductResponse.setMessage("No product found to delete");
                return deleteProductResponse;
            }
            Products productToDelete = products.get();
            productRepository.delete(productToDelete);
            deleteProductResponse.setStatusCode(200);
            deleteProductResponse.setMessage("Product deleted successfully!!");
            return deleteProductResponse;
        } catch (Exception e) {
            deleteProductResponse.setStatusCode(404);
            deleteProductResponse.setMessage("No product found!!");
        }
        return deleteProductResponse;
    }

    @Override
    public GetProductResponse getProductByCategoryName(String category) {
        GetProductResponse getProductResponse = new GetProductResponse();
        try {
            List<Products> product = productRepository.findByCategoryName(category);
            if(product.isEmpty()) {
                getProductResponse.setStatusCode(400);
                getProductResponse.setMessage("Product not found in the category "+ category);
                return getProductResponse;
            }
            List<Products> productToGet = new ArrayList<>(product);
            getProductResponse.setStatusCode(200);
            getProductResponse.setMessage("Product found successfully");
            getProductResponse.setData(productToGet);
            return getProductResponse;
        } catch (Exception e) {
            e.printStackTrace();
            getProductResponse.setStatusCode(500);
            getProductResponse.setMessage("Invalid search!!");
            return getProductResponse;
        }
    }

    @Override
    public GetProductResponse getProductByBrand(String brand) {
        GetProductResponse getProductResponse = new GetProductResponse();
        try {
            List<Products> products = productRepository.findByBrand(brand);
            if(products.isEmpty()) {
                getProductResponse.setStatusCode(400);
                getProductResponse.setMessage("Product with brand name " + brand + " not found");
                return getProductResponse;
            }
            List<Products> productsList = new ArrayList<>(products.size());
            getProductResponse.setStatusCode(200);
            getProductResponse.setMessage("Product  with brand name " + brand + " found successfully");
            getProductResponse.setData(productsList);
            return getProductResponse;
        } catch (Exception e) {
            getProductResponse.setStatusCode(500);
            getProductResponse.setMessage("Invalid search!!");
            return getProductResponse;
        }
    }

    @Override
    public GetProductResponse getProductByCategoryNameAndBrand(String category,String brand) {
        GetProductResponse getProductResponse = new GetProductResponse();
       List<Products> products = productRepository.findByCategoryNameAndBrand(category,brand);
        if(products.isEmpty()) {
            getProductResponse.setStatusCode(400);
            getProductResponse.setMessage("Product with category name "
                    + category + "and brand name " + brand + " not found");
            return getProductResponse;
        }

            List<Products> productToGet = new ArrayList<>(products);
            getProductResponse.setStatusCode(200);
            getProductResponse.setMessage("Product found successfully");
            getProductResponse.setData(productToGet);
            return getProductResponse;
    }
    @Override
    public GetProductResponse getProductByName(String name) {
        GetProductResponse getProductResponse = new GetProductResponse();
        Optional<Products> products = productRepository.findByName(name);
        if(products.isEmpty()) {
            getProductResponse.setStatusCode(404);
            getProductResponse.setMessage("The search name is not available in store!");
            return getProductResponse;
        }
        Products productToGet = products.get();
        getProductResponse.setStatusCode(200);
        getProductResponse.setMessage("Product  with name " + name + " found successfully");
        getProductResponse.setData(productToGet);
        return getProductResponse;
    }

    @Override
    public GetProductResponse getProductByBrandAndName(String brand, String name) {
        GetProductResponse getProductResponse = new GetProductResponse();
        try {
            Optional<Products> products = productRepository.findByBrandAndName(brand,name);
            if(products.isEmpty()) {
                getProductResponse.setStatusCode(404);
                getProductResponse.setMessage("The search brand of product is not available in store!");
                return getProductResponse;
            }
            Products productToGet = products.get();
            getProductResponse.setStatusCode(200);
            getProductResponse.setMessage("Product found successfully");
            getProductResponse.setData(productToGet);
            return getProductResponse;
        } catch (Exception e) {
            getProductResponse.setStatusCode(500);
            getProductResponse.setMessage("Invalid search!!");
            return getProductResponse;
        }
    }

    @Override
    public UpdateProductResponse updateProductPrice(CreateProductRequest createProductRequest, Long id) {
        return null;
    }

    @Override
    public GetProductResponse activateProduct(Long productId) {
        GetProductResponse getProductResponse = new GetProductResponse();
        try {
            Optional<Products> products = productRepository.findById(productId);
            if(products.isEmpty()) {
                getProductResponse.setStatusCode(400);
                getProductResponse.setMessage("Product with Id " + productId + " not found");
                return getProductResponse;
            }
            Products productToGet = products.get();
            productToGet.setAvailable(true);
            productRepository.save(productToGet);

            getProductResponse.setStatusCode(200);
            getProductResponse.setMessage("Product found and activated successfully");
            getProductResponse.setData(productToGet);
            return getProductResponse;
        }
        catch(Exception e) {
            getProductResponse.setStatusCode(500);
            getProductResponse.setMessage("Invalid search!!");
            return getProductResponse;
        }
    }
}
