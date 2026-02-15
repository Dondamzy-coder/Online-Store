package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.OrderRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.OrderResponse;
import com.codewithdondamzy.onlinestore.Models.Order;
import com.codewithdondamzy.onlinestore.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/OnlineStore")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

//    @PreAuthorize("hasAuthority('CUSTOMER')")
//    @PutMapping("/placeOrder/{customerId}")
//    public ResponseEntity<OrderResponse> PlaceOrder(@RequestBody OrderRequest orderRequest,@PathVariable Long customerId) {
//        return ResponseEntity.ok(orderService.placeOrder(orderRequest,customerId));
//    }

    @GetMapping("/getOrderById/{orderId}")
    public  ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/getAllCustomerOrder")
    public ResponseEntity<?> getAllCustomerOrder(@RequestParam Long customerId) {
        return ResponseEntity.ok(orderService.getAllCustomerOrder(customerId));
    }


}

