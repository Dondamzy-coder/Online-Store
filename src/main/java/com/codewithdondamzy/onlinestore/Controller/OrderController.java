package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Response.OrderResponse;
import com.codewithdondamzy.onlinestore.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasAnyAuthority('CUSTOMER','ADMIN')")
    @GetMapping("/getOrderById/{orderId}")
    public  ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PreAuthorize("hasAnyAuthority('CUSTOMER','ADMIN')")
    @GetMapping("/getAllCustomerOrder")
    public ResponseEntity<?> getAllCustomerOrder(@RequestParam Long customerId) {
        return ResponseEntity.ok(orderService.getAllCustomerOrder(customerId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/deleteOrderById/{orderId}")
    public ResponseEntity<OrderResponse> deleteOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.deleteOrderById(orderId));
    }

}

