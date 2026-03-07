package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.ChangePasswordRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerLoginRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.OrderRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.GetCustomerResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.OrderResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.UpdateCustomerResponse;
import com.codewithdondamzy.onlinestore.Service.CustomerService;
import com.codewithdondamzy.onlinestore.Service.OrderService;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/OnlineStore")
public class CustomerController {
    private final CustomerService customerService;
    private final OrderService orderService;
    private final AuthenticationManager authenticationManager;

    public CustomerController(CustomerService customerService, OrderService orderService, AuthenticationManager authenticationManager) {
        this.customerService = customerService;
        this.orderService = orderService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/createCustomer")
    public ResponseEntity<?> createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        return ResponseEntity.ok(customerService.createCustomer(createCustomerRequest));
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/customerLogin")
    public ResponseEntity<?> customerLogin(@RequestBody CreateCustomerLoginRequest createCustomerLoginRequest) {
        return ResponseEntity.ok(customerService.customerLogin(createCustomerLoginRequest));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PutMapping("/changeCustomerPassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, Authentication authentication) {
       UpdateCustomerResponse customer = customerService.changePassword(changePasswordRequest,authentication);
       return ResponseEntity.ok(customer);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PostMapping("/setAddressAsDefault/{id}")
    public ResponseEntity<?> setAddressAsDefault(@PathVariable Long id,Authentication authentication) {
        UpdateCustomerResponse newCustomerAddress = customerService.setAddressAsDefault(id,authentication);
        return ResponseEntity.ok(newCustomerAddress);
    }

    @PreAuthorize("hasAnyAuthority('CUSTOMER','ADMIN')")
    @PutMapping("/addItemToCart/{productId}/{cartId}/")
    public ResponseEntity<?> addItemToCart(@PathVariable Long productId,@PathVariable Long cartId,@RequestParam int quantity) {
        return ResponseEntity.ok(customerService.addItemToCart(productId,cartId,quantity));
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/getAllCustomers")
    public ResponseEntity<?> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/getCustomerByEmail")
    public ResponseEntity<?> getCustomerByEmail(Authentication authentication) {
        return ResponseEntity.ok(customerService.getCustomerByEmailAddress(authentication));
    }

//    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/updateCustomerById/{id}")
    public ResponseEntity<?> updateCustomerById(@RequestBody CreateCustomerRequest createCustomerRequest,@PathVariable Long id ) {
        return ResponseEntity.ok(customerService.updateCustomerById(createCustomerRequest,id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/deleteCustomerById/{id}")
    public ResponseEntity<?> deleteCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.deleteCustomer(id));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    @GetMapping("/getAllOrders")
    public ResponseEntity<?> getAllOrders() {
        GetCustomerResponse allOrders = customerService.getAllOrders();
        return ResponseEntity.ok(allOrders);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PutMapping("/addReview/{reviewId}/{productId}")
    public ResponseEntity<?> addReview(@PathVariable Long reviewId,@PathVariable Long productId) {
        UpdateCustomerResponse review = customerService.addReview(reviewId,productId);
        return ResponseEntity.ok(review);
    }

    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN')")
    @PutMapping("/placeOrder/{customerId}")
    public ResponseEntity<OrderResponse> PlaceOrder(@RequestBody OrderRequest orderRequest, @PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.placeOrder(orderRequest,customerId));
    }
}
