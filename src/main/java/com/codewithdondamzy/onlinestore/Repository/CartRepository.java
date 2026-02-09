package com.codewithdondamzy.onlinestore.Repository;

import com.codewithdondamzy.onlinestore.Models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    void deleteCartById(Long id);

    Cart getCartByCustomer_Id(Long customerId);

    Cart findCartById(Long id);
}
