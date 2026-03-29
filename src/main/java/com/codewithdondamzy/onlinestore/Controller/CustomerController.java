package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.*;
import com.codewithdondamzy.onlinestore.Dtos.Response.OrderResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.UpdateCustomerResponse;
import com.codewithdondamzy.onlinestore.service.CustomerService;
import com.codewithdondamzy.onlinestore.service.OrderService;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<?> getAllCustomers(Pageable pageable) {
        return ResponseEntity.ok(customerService.getAllCustomers(pageable));
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
    public ResponseEntity<?> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(customerService.getAllOrders(pageable));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PutMapping("/addReviewToProduct")
    public ResponseEntity<?> addReviewToProduct(@RequestBody ReviewRequest reviewRequest,
                                                Authentication authentication,@RequestParam String productName) {
        return ResponseEntity.ok(customerService.addReviewToProduct(authentication,reviewRequest,productName));
    }

    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN')")
    @PutMapping("/placeOrder/{customerId}")
    public ResponseEntity<OrderResponse> PlaceOrder(@RequestBody OrderRequest orderRequest, @PathVariable Long customerId,
                                                    Authentication authentication) {
        return ResponseEntity.ok(orderService.placeOrder(orderRequest,customerId,authentication));
    }
}
