package com.codewithdondamzy.onlinestore.Dtos.Response;

import com.codewithdondamzy.onlinestore.Models.CartItem;
import lombok.Data;

@Data
public class CartItemResponse {
    private int statusCode;
    private String message;
    private Object data;
}
