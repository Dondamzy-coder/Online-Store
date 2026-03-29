package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Response.CartItemResponse;

public interface CartItemService {
    CartItemResponse addItemToCart(Long cartId, Long productId, int quantity);

    CartItemResponse removeItemFromCart(Long cartId,Long productId);

    CartItemResponse updateCartItemQuantity(Long cartId,Long productId, int quantity);
}
