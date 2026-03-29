package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Response.CartResponse;

public interface CartService {

    CartResponse getCartsById(Long id);

    CartResponse clearCartById(Long id);

    CartResponse addProductToCart(Long cartId, Long productId,int quantity);

    CartResponse getCartTotalPrice(Long id);

}
