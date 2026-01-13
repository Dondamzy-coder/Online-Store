package com.codewithdondamzy.onlinestore.Dtos.Response;

import lombok.Data;

@Data
public class ReviewResponse {
    private int statusCode;
    private String message;
    private Object data;
}
