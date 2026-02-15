package com.codewithdondamzy.onlinestore.Dtos.Response;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerRequest;
import com.codewithdondamzy.onlinestore.Models.Customer;
import lombok.Data;

@Data
public class CreateCustomerResponse {
    private String jwtToken;
    private int statusCode;
    private String message;
    private Object data;
}
