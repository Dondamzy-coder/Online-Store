package com.codewithdondamzy.onlinestore.Dtos.Response;

import com.codewithdondamzy.onlinestore.Models.PaymentMethod;
import com.codewithdondamzy.onlinestore.Models.PaymentStatus;
import lombok.Data;

@Data
public class PaymentResponse {
    private int statusCode;
    private String authorizationUrl;
    private String referenceNumber;
    private String description;
    private PaymentMethod method;
    private PaymentStatus status;
}
