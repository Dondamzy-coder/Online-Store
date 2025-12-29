package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerLoginRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.CreateCustomerResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.GetCustomerResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.UpdateCustomerResponse;
import com.codewithdondamzy.onlinestore.Models.Customer;
import com.codewithdondamzy.onlinestore.Service.CustomerService;
import com.codewithdondamzy.onlinestore.jwt.JwtUtils;
import org.apache.catalina.Authenticator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/OnlineStore")
public class CustomerController {
    private final CustomerService customerService;
    private final JwtUtils jwtUtils;
    private AuthenticationManager authenticationManager;

    public CustomerController(CustomerService customerService, JwtUtils jwtUtils) {
        this.customerService = customerService;
        this.jwtUtils = jwtUtils;
    }
    @PostMapping("/createCustomer")
    public ResponseEntity<?> createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        return ResponseEntity.ok(customerService.createCustomer(createCustomerRequest));
    }

    @PostMapping("/customerLogin")
    public ResponseEntity<?> customerLogin(@RequestBody CreateCustomerLoginRequest createCustomerLoginRequest) {
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    createCustomerLoginRequest.getUserName(),createCustomerLoginRequest.getPassword()));
        } catch (AuthenticationException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Bad credentials");
            body.put("Status", false);
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUserName(userDetails);

        CreateCustomerResponse response = new CreateCustomerResponse();
        return ResponseEntity.ok(customerService.customerLogin(createCustomerLoginRequest));
    }

    @PutMapping("/changeCustomerPassword")
    public ResponseEntity<?> changePassword(@RequestParam String oldPassword,
                                            @RequestParam String newPassword,Authentication authentication) {
       UpdateCustomerResponse customer = customerService.changePassword(oldPassword,newPassword,authentication);
       return ResponseEntity.ok(customer);
    }

    @PostMapping("/setAddressAsDefault/{id}")
    public ResponseEntity<?> setAddressAsDefault(@PathVariable Long id,Authentication authentication) {
        UpdateCustomerResponse newCustomerAddress = customerService.setAddressAsDefault(id,authentication);
        return ResponseEntity.ok(newCustomerAddress);
    }

    @PutMapping("/addItemToCart/{productId}/{cartId}")
    public ResponseEntity<?> addItemToCart(@PathVariable Long productId,@PathVariable Long cartId,@RequestParam int quantity) {
        return ResponseEntity.ok(customerService.addItemToCart(productId,cartId,quantity));
    }


    @GetMapping("getAllCustomers")
    public ResponseEntity<?> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/getCustomerByEmail")
    public ResponseEntity<?> getCustomerByEmail(@RequestParam String emailAddress) {
        return ResponseEntity.ok(customerService.getCustomerByEmailAddress(emailAddress));
    }
    @PutMapping("/updateCustomerByEmail}")
    public ResponseEntity<?> updateCustomerById(@RequestBody CreateCustomerRequest createCustomerRequest,
                                                @RequestParam String emailAddress) {
        return ResponseEntity.ok(customerService.updateCustomer(createCustomerRequest,emailAddress));
    }

    @DeleteMapping("/DeleteCustomerById{id}")
    public ResponseEntity<?> deleteCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.deleteCustomer(id));
    }

    @GetMapping("/getAllOrders")
    public ResponseEntity<?> getAllOrders() {
        GetCustomerResponse allOrders = customerService.getAllOrders();
        return ResponseEntity.ok(allOrders);
    }

    @PutMapping("/addReview/{productId}")
    public ResponseEntity<?> addReview(@PathVariable Long productId) {
        UpdateCustomerResponse review = customerService.addReview(productId);
        return ResponseEntity.ok(review);
    }
}
