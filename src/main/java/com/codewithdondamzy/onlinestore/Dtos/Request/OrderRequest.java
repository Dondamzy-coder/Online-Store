package com.codewithdondamzy.onlinestore.Dtos.Request;

import com.codewithdondamzy.onlinestore.Models.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderRequest {
    private String customerId;
    private String customerName;
    private LocalDate orderDate;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;
    private List<OrderItemRequest> orderItemRequests;
}
