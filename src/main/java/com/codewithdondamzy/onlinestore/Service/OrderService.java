package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.OrderRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest orderRequest,Long customerId);

    OrderResponse getOrderById(Long orderId);

    OrderResponse getAllUserOrder(Long userId);
}
