package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateProductRequest;
import com.codewithdondamzy.onlinestore.Service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/OnlineStore")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/createProduct")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        return ResponseEntity.ok(productService.createProduct(createProductRequest));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/getProduct")
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/getProductById/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/updateProductById/{id}")
    public ResponseEntity<?> updateProductById(@RequestBody CreateProductRequest createProductRequest,
                                               @PathVariable Long id) {
        return ResponseEntity.ok(productService.updateProductById(createProductRequest,id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/deleteProductById/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteProductById(id));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/getProductByCategoryName")
    public ResponseEntity<?> getProductByCategoryName(@RequestParam String categoryName) {
        return ResponseEntity.ok(productService.getProductByCategoryName(categoryName));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/getProductByBrand")
    public ResponseEntity<?> getProductByBrand(@RequestParam String brand) {
        return ResponseEntity.ok(productService.getProductByBrand(brand));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/getProductByCategoryNameAndBrand")
    public ResponseEntity<?> getProductByCategoryNameAndBrand(@RequestParam String categoryName,@RequestParam String brand) {
        return ResponseEntity.ok(productService.getProductByCategoryNameAndBrand(categoryName,brand));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/getProductByName")
    public ResponseEntity<?> getProductByName(@RequestParam String name) {
        return ResponseEntity.ok(productService.getProductByName(name));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/getProductByBrandAndName")
    public ResponseEntity<?> getProductByBrandAndName(@RequestParam String brand, @RequestParam String name) {
        return ResponseEntity.ok(productService.getProductByBrandAndName(brand,name));
    }
}
