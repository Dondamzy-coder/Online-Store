package com.codewithdondamzy.onlinestore.Dtos.Response;

import com.codewithdondamzy.onlinestore.Models.Customer;
import lombok.Data;

import java.util.List;

@Data
public class GetCustomerResponse {
    private int statusCode;
    private String message;
    private List<Customer> customer;
    private Object data;
}
