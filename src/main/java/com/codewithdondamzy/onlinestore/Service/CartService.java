package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Response.CartResponse;
import com.codewithdondamzy.onlinestore.Models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

public interface CartService {

    CartResponse getCartsById(Long id);

    CartResponse clearCartById(Long id);

    CartResponse addProductToCart(Long cartId, Long productId,int quantity);

    CartResponse getCartTotalPrice(Long id);


    Cart getCartByCustomerId(Long customerId);
}
