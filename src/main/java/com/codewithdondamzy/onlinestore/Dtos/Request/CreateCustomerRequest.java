package com.codewithdondamzy.onlinestore.Dtos.Request;

import com.codewithdondamzy.onlinestore.Models.Address;
import lombok.Data;

import java.util.List;

@Data
public class CreateCustomerRequest {
    private String name;
    private List<Address> address;
    private String email;
    private String userName;

    public static boolean isValidPassword(String password) {
        return false;
    }
}
