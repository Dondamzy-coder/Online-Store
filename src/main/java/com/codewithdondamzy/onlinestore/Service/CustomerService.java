package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.ChangePasswordRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerLoginRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.CreateCustomerResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.DeleteCustomerResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.GetCustomerResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.UpdateCustomerResponse;
import com.codewithdondamzy.onlinestore.Models.Address;
import org.springframework.security.core.Authentication;

public interface CustomerService {

    CreateCustomerResponse createCustomer(CreateCustomerRequest createCustomerRequest);

    CreateCustomerResponse customerLogin(CreateCustomerLoginRequest createCustomerLoginRequest);

    UpdateCustomerResponse changePassword(ChangePasswordRequest changePasswordRequest,Authentication authentication);

    UpdateCustomerResponse setAddressAsDefault(Long addressId,Authentication authentication);

    UpdateCustomerResponse addItemToCart(Long productId, Long cartId, int quantity);

    GetCustomerResponse getAllCustomers();

    GetCustomerResponse getCustomerByEmailAddress(Authentication authentication);

    UpdateCustomerResponse updateCustomerById(CreateCustomerRequest createCustomerRequest,Long id);

    DeleteCustomerResponse deleteCustomer(Long id);

    GetCustomerResponse getAllOrders();

    UpdateCustomerResponse addReview(Long reviewId,Long productId);

    UpdateCustomerResponse updateReview(Long productId);

    UpdateCustomerResponse manageCustomer(String emailAddress, Authentication authentication);

}
