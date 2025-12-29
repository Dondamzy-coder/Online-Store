package com.codewithdondamzy.onlinestore.Dtos.Response;

import lombok.Data;

@Data
public class AdminResponse {
    private int statusCode;
    private String message;
    private Object data;
}
