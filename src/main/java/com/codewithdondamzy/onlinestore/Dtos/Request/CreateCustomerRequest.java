package com.codewithdondamzy.onlinestore.Dtos.Request;

import com.codewithdondamzy.onlinestore.Models.Address;
import com.codewithdondamzy.onlinestore.Models.Role;
import lombok.Data;

import java.util.List;

@Data
public class CreateCustomerRequest {
    private String name;
    private List<AddressRequest> address;
    private String email;
    private String userName;
    private String phoneNumber;
    private List<OrderRequest> orderRequests;
    private String password;
    private CartRequest cartRequest;
    private Role role;

    public static boolean isValidPassword(String password) {
        return true;
    }
}
