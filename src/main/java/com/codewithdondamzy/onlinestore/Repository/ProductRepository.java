package com.codewithdondamzy.onlinestore.Repository;

import com.codewithdondamzy.onlinestore.Dtos.Response.GetProductResponse;
import com.codewithdondamzy.onlinestore.Models.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Products, Long> {

    Optional<Products> findProductsById(Long id);

    Optional<Products> findByCategoryName(String category);

    Optional<Products> findByBrand(String brand);

    Optional<Products> findByCategoryNameAndBrand(String category, String brand);

    Optional<Products> findByName(String name);

    Optional<Products> findByBrandAndName(String brand, String name);
}
