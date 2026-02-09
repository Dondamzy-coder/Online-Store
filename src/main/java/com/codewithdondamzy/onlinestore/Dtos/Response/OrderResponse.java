package com.codewithdondamzy.onlinestore.Dtos.Response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderResponse {
    private int statusCode;
    private String message;
    private Long orderId;
    private BigDecimal totalAmount;
    private String data;
}
