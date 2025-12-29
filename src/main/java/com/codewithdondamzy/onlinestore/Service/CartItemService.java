package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.CartItemRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.CartItemResponse;

public interface CartItemService {
    CartItemResponse addItemToCart(Long cartId, Long productId, int quantity);

    CartItemResponse removeItemFromCart(Long cartId,Long productId);

    CartItemResponse updateCartItemQuantity(Long cartId,Long productId, int quantity);
}
