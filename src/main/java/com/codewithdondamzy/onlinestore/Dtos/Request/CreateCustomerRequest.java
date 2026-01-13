package com.codewithdondamzy.onlinestore.Dtos.Request;

import com.codewithdondamzy.onlinestore.Models.Address;
import lombok.Data;

import java.util.List;

@Data
public class CreateCustomerRequest {
    private String name;
    private List<AddressRequest> address;
    private String email;
    private String userName;
    private List<OrderRequest> orderRequests;
    private String password;
    private CartRequest cartRequest;

    public static boolean isValidPassword(String password) {
        return true;
    }
}
