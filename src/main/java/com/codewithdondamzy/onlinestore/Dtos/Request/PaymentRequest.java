package com.codewithdondamzy.onlinestore.Dtos.Request;

import com.codewithdondamzy.onlinestore.Models.PaymentMethod;
import com.codewithdondamzy.onlinestore.Models.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String description;
    private String referenceNumber;
}
