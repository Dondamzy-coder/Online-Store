package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.ChangePasswordRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerLoginRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.GetCustomerResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.UpdateCustomerResponse;
import com.codewithdondamzy.onlinestore.Service.CustomerService;
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
    private final AuthenticationManager authenticationManager;

    public CustomerController(CustomerService customerService, AuthenticationManager authenticationManager) {
        this.customerService = customerService;
        this.authenticationManager = authenticationManager;
    }
    @PostMapping("/createCustomer")
    public ResponseEntity<?> createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        return ResponseEntity.ok(customerService.createCustomer(createCustomerRequest));
    }

    @PostMapping("/customerLogin")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> customerLogin(@RequestBody CreateCustomerLoginRequest createCustomerLoginRequest) {
        return ResponseEntity.ok(customerService.customerLogin(createCustomerLoginRequest));
    }

    @PutMapping("/changeCustomerPassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, Authentication authentication) {
       UpdateCustomerResponse customer = customerService.changePassword(changePasswordRequest,authentication);
       return ResponseEntity.ok(customer);
    }

    @PostMapping("/setAddressAsDefault/{id}")
    public ResponseEntity<?> setAddressAsDefault(@PathVariable Long id,Authentication authentication) {
        UpdateCustomerResponse newCustomerAddress = customerService.setAddressAsDefault(id,authentication);
        return ResponseEntity.ok(newCustomerAddress);
    }

    @PutMapping("/addItemToCart/{productId}/{cartId}/")
    public ResponseEntity<?> addItemToCart(@PathVariable Long productId,@PathVariable Long cartId,@RequestParam int quantity) {
        return ResponseEntity.ok(customerService.addItemToCart(productId,cartId,quantity));
    }


    @GetMapping("/getAllCustomers")
    public ResponseEntity<?> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/getCustomerByEmail")
    public ResponseEntity<?> getCustomerByEmail(Authentication authentication) {
        return ResponseEntity.ok(customerService.getCustomerByEmailAddress(authentication));
    }
    @PutMapping("/updateCustomerByEmail")
    public ResponseEntity<?> updateCustomerById(@RequestBody CreateCustomerRequest createCustomerRequest,
                                                 Authentication authentication) {
        return ResponseEntity.ok(customerService.updateCustomer(createCustomerRequest,authentication));
    }

    @DeleteMapping("/deleteCustomerById/{id}")
    public ResponseEntity<?> deleteCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.deleteCustomer(id));
    }

    @GetMapping("/getAllOrders")
    public ResponseEntity<?> getAllOrders() {
        GetCustomerResponse allOrders = customerService.getAllOrders();
        return ResponseEntity.ok(allOrders);
    }

    @PutMapping("/addReview/{reviewId}/{productId}")
    public ResponseEntity<?> addReview(@PathVariable Long reviewId,@PathVariable Long productId) {
        UpdateCustomerResponse review = customerService.addReview(reviewId,productId);
        return ResponseEntity.ok(review);
    }
}
