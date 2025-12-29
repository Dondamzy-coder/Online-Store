package com.codewithdondamzy.onlinestore.Dtos.Response;

import lombok.Data;

@Data
public class OrderResponse {
    private int statusCode;
    private String message;
    private String data;
}
