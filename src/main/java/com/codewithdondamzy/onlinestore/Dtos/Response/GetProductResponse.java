package com.codewithdondamzy.onlinestore.Dtos.Response;

import com.codewithdondamzy.onlinestore.Models.Products;
import lombok.Data;

import java.util.List;

@Data
public class GetProductResponse {
    private int statusCode;
    private String message;
    private Object data;
}
