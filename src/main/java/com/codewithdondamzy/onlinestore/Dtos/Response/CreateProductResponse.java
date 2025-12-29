package com.codewithdondamzy.onlinestore.Dtos.Response;

import com.codewithdondamzy.onlinestore.Models.Products;
import lombok.Data;

@Data
public class CreateProductResponse {
    private int statusCode;
    private String message;
    private Products product;
}
