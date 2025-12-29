package com.codewithdondamzy.onlinestore.Dtos.Request;

import lombok.Data;

@Data
public class CreateCustomerLoginRequest {
    private String userName;
    private String emailAddress;
    private String password;
}
