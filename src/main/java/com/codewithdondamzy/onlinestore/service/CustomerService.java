package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Request.ChangePasswordRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerLoginRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.ReviewRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface CustomerService {

    CreateCustomerResponse createCustomer(CreateCustomerRequest createCustomerRequest);

    CreateCustomerResponse customerLogin(CreateCustomerLoginRequest createCustomerLoginRequest);

    UpdateCustomerResponse changePassword(ChangePasswordRequest changePasswordRequest,Authentication authentication);

    UpdateCustomerResponse setAddressAsDefault(Long addressId,Authentication authentication);

    UpdateCustomerResponse addItemToCart(Long productId, Long cartId, int quantity);

    GetCustomerResponse getAllCustomers(Pageable pageable);

    GetCustomerResponse getCustomerByEmailAddress(Authentication authentication);

    UpdateCustomerResponse updateCustomerById(CreateCustomerRequest createCustomerRequest,Long id);

    DeleteCustomerResponse deleteCustomer(Long id);

    GetCustomerResponse getAllOrders(Pageable pageable);

    ReviewResponse addReviewToProduct(Authentication authentication, ReviewRequest reviewRequest, String productName);


    UpdateCustomerResponse updateReview(Long productId);

    UpdateCustomerResponse manageCustomer(String emailAddress, Authentication authentication);

}
