package com.codewithdondamzy.onlinestore.Repository;

import com.codewithdondamzy.onlinestore.Models.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Products, Long> {

    Optional<Products> findProductsById(Long id);

    List<Products> findByCategoryName(String category);

   List<Products> findByBrand(String brand);

    List<Products> findByCategoryNameAndBrand(String category, String brand);

    Optional<Products> findByName(String name);

    Optional<Products> findByBrandAndName(String brand, String name);


}
