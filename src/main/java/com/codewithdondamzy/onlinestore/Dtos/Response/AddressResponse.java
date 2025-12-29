package com.codewithdondamzy.onlinestore.Dtos.Response;

import lombok.Data;

@Data
public class AddressResponse {
    private int statusCode;
    private String message;
    private Object data;
}
