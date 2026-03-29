package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Request.OrderRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.OrderResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest orderRequest, Long customerId, Authentication authentication);

    OrderResponse getOrderById(Long orderId);

    OrderResponse deleteOrderById(Long orderId);

    OrderResponse getAllCustomerOrder(Long customerId);
}
