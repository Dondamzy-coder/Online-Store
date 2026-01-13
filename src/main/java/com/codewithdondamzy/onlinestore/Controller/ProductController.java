package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateProductRequest;
import com.codewithdondamzy.onlinestore.Service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/OnlineStore")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @PostMapping("/createProduct")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        return ResponseEntity.ok(productService.createProduct(createProductRequest));
    }
    @PostMapping("/getProduct")
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    @GetMapping("//getProductById{id}")
    public ResponseEntity<?> getProductById(@RequestBody CreateProductRequest createProductRequest,@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    @PutMapping("/updateProductById{id}/{categoryId}")
    public ResponseEntity<?> updateProductById(@RequestBody CreateProductRequest createProductRequest,
                                               @PathVariable Long id,@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.updateProductById(createProductRequest,id,categoryId));
    }
    @DeleteMapping("/deleteProductById{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteProductById(id));
    }
    @GetMapping("/getProductByCategoryName")
    public ResponseEntity<?> getProductByCategoryName(@RequestParam String categoryName) {
        return ResponseEntity.ok(productService.getProductByCategoryName(categoryName));
    }
    @GetMapping("/getProductByBrand")
    public ResponseEntity<?> getProductByBrand(@RequestParam String brand) {
        return ResponseEntity.ok(productService.getProductByBrand(brand));
    }
    @GetMapping("/getProductByCategoryNameAndBrand")
    public ResponseEntity<?> getProductByCategoryNameAndBrand(@RequestParam String categoryName,@RequestParam String brand) {
        return ResponseEntity.ok(productService.getProductByCategoryNameAndBrand(categoryName,brand));
    }
    @GetMapping("/getProductByName")
    public ResponseEntity<?> getProductByName(@RequestParam String name) {
        return ResponseEntity.ok(productService.getProductByName(name));
    }
    @GetMapping("/getProductByBrandAndName")
    public ResponseEntity<?> getProductByBrandAndName(@RequestParam String brand, @RequestParam String name) {
        return ResponseEntity.ok(productService.getProductByBrandAndName(brand,name));
    }
}
