package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateProductRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.CreateProductResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.DeleteProductResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.GetProductResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.UpdateProductResponse;
import com.codewithdondamzy.onlinestore.Models.Category;
import com.codewithdondamzy.onlinestore.Models.Products;
import com.codewithdondamzy.onlinestore.Repository.CategoryRepository;
import com.codewithdondamzy.onlinestore.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
            Products product = Products.builder()
                    .name(createProductRequest.getName())
                    .description(createProductRequest.getDescription())
                    .price(createProductRequest.getPrice())
                    .build();
//            product.setName(createProductRequest.getName());
//            product.setPrice(createProductRequest.getPrice());
//            product.setDescription(createProductRequest.getDescription());
            productRepository.save(product);
            createProductResponse.setStatusCode(200);
            createProductResponse.setMessage("Product created successfully!!");
            createProductResponse.setProduct(product);
            return createProductResponse;
        } catch (Exception e) {
            createProductResponse.setStatusCode(400);
            createProductResponse.setMessage("Invalid input.Product cannot be created");
        }
        return createProductResponse;
    }

    @Override
    public GetProductResponse getAllProducts() {
        GetProductResponse getProductResponse = new GetProductResponse();
        try {
            List<Products> existingProducts = productRepository.findAll();
            if(existingProducts.isEmpty()) {
                getProductResponse.setStatusCode(400);
                getProductResponse.setMessage("Products not available");
                return getProductResponse;
            }
            List<Products> product = existingProducts.stream().toList();
            getProductResponse.setStatusCode(200);
            getProductResponse.setMessage("Products gotten successfully!!");
            getProductResponse.setData(product);
            return getProductResponse;
        } catch (Exception e) {
            getProductResponse.setStatusCode(500);
            getProductResponse.setMessage("Product not available");
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
            Optional<Products> products = productRepository.findProductsById(id);
            if(products.isEmpty()) {
                updateProductResponse.setStatusCode(400);
                updateProductResponse.setMessage("Product not found");
                return updateProductResponse;
            }
            Products productToUpDate = products.get();
            productToUpDate.setName(createProductRequest.getName());
            productToUpDate.setPrice(createProductRequest.getPrice());
            productToUpDate.setDescription(createProductRequest.getDescription());
            productToUpDate.setCategory(createProductRequest.getCategory());
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
            Optional<Products> product = productRepository.findByCategoryName(category);
            if(product.isEmpty()) {
                getProductResponse.setStatusCode(400);
                getProductResponse.setMessage("Product not found in the category "+ category);
                return getProductResponse;
            }
            Products productToGet = product.get();
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
    public GetProductResponse getProductByBrand(String brand) {
        GetProductResponse getProductResponse = new GetProductResponse();
        Optional<Products> products = productRepository.findByBrand(brand);
        productRepository.findByBrand(brand);
        return getProductResponse;
    }

    @Override
    public GetProductResponse getProductByCategoryNameAndBrand(String category, String brand) {
        GetProductResponse getProductResponse = new GetProductResponse();
        Optional<Products> products = productRepository.findByCategoryNameAndBrand(category, brand);
        if (products.isEmpty()) {
            getProductResponse.setStatusCode(404);
            getProductResponse.setMessage("The search brand of product is not available in store!");
            getProductResponse.setData(products);
        }
        if (products.isPresent()) {
            Products productToGet = products.get();
            getProductResponse.setStatusCode(200);
            getProductResponse.setMessage("Product found successfully");
            getProductResponse.setData(productToGet);
            return getProductResponse;
        }
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
    public GetProductResponse activateProduct() {
        return null;
    }
}
