package com.codewithdondamzy.onlinestore.Repository;

import com.codewithdondamzy.onlinestore.Models.Cart;
import com.codewithdondamzy.onlinestore.Models.CartItem;
import com.codewithdondamzy.onlinestore.Models.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem deleteAllByCartId(Long cart_id);

    Optional<CartItem> findByCartAndProduct(Cart cart, Optional<Products> product);
}
