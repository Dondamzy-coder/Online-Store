package com.codewithdondamzy.onlinestore.Dtos.Request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemRequest {
    private int quantity;
    private BigDecimal price;
    private String productId;
    private Long cartId;
}
