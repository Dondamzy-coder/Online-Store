package com.codewithdondamzy.onlinestore.Dtos.Request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminRequest {
    private String userName;
    private String UUID;
    private String email;
}
