package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerLoginRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.CreateCustomerResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.DeleteCustomerResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.GetCustomerResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.UpdateCustomerResponse;
import com.codewithdondamzy.onlinestore.Models.*;
import com.codewithdondamzy.onlinestore.Repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, AddressRepository addressRepository, ProductRepository productRepository, CartRepository cartRepository, CartItemRepository cartItemRepository){
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }


    @Override
    public CreateCustomerResponse createCustomer(CreateCustomerRequest createCustomerRequest) {
        CreateCustomerResponse creatCustomerResponse = new CreateCustomerResponse();
        try {
            Optional<Customer> newCustomer = customerRepository.findCustomerByEmail(createCustomerRequest.getEmail());
            if(newCustomer.isPresent()){
                creatCustomerResponse.setStatusCode(400);
                creatCustomerResponse.setMessage("Customer already exists");
                return creatCustomerResponse;
            }
            Customer customer = Customer.builder()
                    .name(createCustomerRequest.getName())
                    .email(createCustomerRequest.getEmail())
                    .addresses(createCustomerRequest.getAddress())
                    .userName(createCustomerRequest.getUserName())
                    .UUID(UUID.randomUUID().toString())
                    .build();
//            customer.setName(createCustomerRequest.getName());
//            customer.setEmail(createCustomerRequest.getEmail());
//            customer.setAddress(createCustomerRequest.getAddress());
//            customer.setUserName(createCustomerRequest.getUserName());
//            customer.setUUID(UUID.randomUUID().toString());
            customerRepository.save(customer);
            creatCustomerResponse.setStatusCode(200);
            creatCustomerResponse.setMessage("Customer created successfully!!");
            return creatCustomerResponse;
        } catch (Exception e) {
            creatCustomerResponse.setStatusCode(500);
            creatCustomerResponse.setMessage("Internal Server Error,please try again later!!");
        }
        return creatCustomerResponse;
    }

    @Override
    public CreateCustomerResponse customerLogin(CreateCustomerLoginRequest createCustomerLoginRequest) {
        CreateCustomerResponse createCustomerResponse = new CreateCustomerResponse();
        try {
            Optional<Customer> existingCustomer = customerRepository.findCustomerByEmail(createCustomerLoginRequest.getEmailAddress());
            if(existingCustomer.isPresent()) {
                throw new IllegalArgumentException("Customer with email:"
                        + createCustomerLoginRequest.getEmailAddress() + "already exists!");
            }
            Customer customerToLogin = existingCustomer.get();
            if(!createCustomerLoginRequest.getUserName().equals(customerToLogin.getUserName())) {
                throw new IllegalArgumentException("Invalid username for email" + createCustomerLoginRequest.getEmailAddress());
            }
            createCustomerResponse.setStatusCode(200);
            createCustomerResponse.setMessage("Customer login successfully!!");
            createCustomerResponse.setCustomer(customerToLogin);
            return createCustomerResponse;
        } catch (IllegalArgumentException e) {
            createCustomerResponse.setStatusCode(500);
            createCustomerResponse.setMessage("Invalid input try again!!");
        }
        return createCustomerResponse;
    }

    @Override
    public UpdateCustomerResponse changePassword(String oldPassword, String newPassword, Authentication authentication) {
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        String emailAddress = authentication.getName();
        try {
            Optional<Customer> customer = customerRepository.findCustomerByEmail(emailAddress);
            if(customer.isEmpty()) {
                updateCustomerResponse.setStatusCode(400);
                updateCustomerResponse.setMessage("Customer not found!");
                return updateCustomerResponse;
            }
            Customer existingCustomer = customer.get();
            if(!passwordEncoder.matches(oldPassword,existingCustomer.getPassword())) {
                updateCustomerResponse.setStatusCode(400);
                updateCustomerResponse.setMessage("Old password doesn't match!");
                return updateCustomerResponse;
            }
            if(passwordEncoder.matches(newPassword,existingCustomer.getPassword())) {
                updateCustomerResponse.setStatusCode(400);
                updateCustomerResponse.setMessage("New password must be different from the old password!");
                return updateCustomerResponse;
            }

            existingCustomer.setPassword(passwordEncoder.encode(newPassword));
            customerRepository.save(existingCustomer);
            updateCustomerResponse.setStatusCode(200);
            updateCustomerResponse.setMessage("Customer password updated successfully!!");
            return updateCustomerResponse;
        } catch (Exception e) {
            updateCustomerResponse.setStatusCode(500);
            updateCustomerResponse.setMessage("Unexpected error occurred!!");
            return updateCustomerResponse;
        }
    }

    @Override
    public UpdateCustomerResponse setAddressAsDefault(Long addressId,Authentication authentication) {
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        String emailAddress = authentication.getName();
        try {
            Optional<Customer> customer = customerRepository.findCustomerByEmail(emailAddress);
            Optional<Address> address = addressRepository.findById(addressId);
            if (customer.isEmpty()) {
                updateCustomerResponse.setStatusCode(400);
                updateCustomerResponse.setMessage("Customer not found!");
                return updateCustomerResponse;
            }
            if (address.isEmpty()) {
                updateCustomerResponse.setStatusCode(400);
                updateCustomerResponse.setMessage("Address not found!");
                return updateCustomerResponse;
            }
            Customer existingCustomer = customer.get();
            Address existingAddress = address.get();
            if (!existingAddress.getCustomer().getId().equals(existingCustomer.getId())) {
                updateCustomerResponse.setStatusCode(400);
                updateCustomerResponse.setMessage("Address does not belong to this customer!");
                return updateCustomerResponse;
            }
            existingCustomer.getAddresses()
                    .forEach(address1 -> address1.setDefault(false));

            existingAddress.setDefault(true);
            addressRepository.save(existingAddress);
            updateCustomerResponse.setStatusCode(200);
            updateCustomerResponse.setMessage("Customer address updated and set as default successfully!!");
            return updateCustomerResponse;
        } catch (Exception e) {
            updateCustomerResponse.setStatusCode(500);
            updateCustomerResponse.setMessage("Unexpected error occurred!!");
            return updateCustomerResponse;
        }
    }

    @Override
    public UpdateCustomerResponse addItemToCart(Long productId, Long cartId, int quantity) {
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        try {
            Optional<Products> product = productRepository.findProductsById(productId);
            Optional<Cart> cart = cartRepository.findById(cartId);
            if (product.isEmpty()) {
                updateCustomerResponse.setStatusCode(401);
                updateCustomerResponse.setMessage("Product not found,cannot be added to cart!");
                return updateCustomerResponse;
            }
            if (cart.isEmpty()) {
                updateCustomerResponse.setStatusCode(401);
                updateCustomerResponse.setMessage("Cart not found,product cannot be added to cart!");
                return updateCustomerResponse;
            }
            Products productToAdd = product.get();
            Cart cartToAdd = cart.get();
            CartItem cartItem = new CartItem();
            cartItem.setProduct(productToAdd);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cartToAdd);
            cartItem.setTotalPrice();
            cartItemRepository.save(cartItem);
            cartRepository.save(cartToAdd);
            updateCustomerResponse.setStatusCode(200);
            updateCustomerResponse.setMessage("Product added successfully!!");
            return updateCustomerResponse;
        } catch (Exception e) {
            updateCustomerResponse.setStatusCode(500);
            updateCustomerResponse.setMessage("Unexpected error occurred!!");
            return updateCustomerResponse;
        }
    }

    @Override
    public GetCustomerResponse getAllCustomers() {
        GetCustomerResponse getCustomerResponse = new GetCustomerResponse();
        try {
            List<Customer> customerList = customerRepository.findAll();
            if(customerList.isEmpty()) {
                getCustomerResponse.setStatusCode(400);
                getCustomerResponse.setMessage("No customer found!!");
                return getCustomerResponse;
            }
            Customer customerToGet = customerList.get(20);
            getCustomerResponse.setStatusCode(200);
            getCustomerResponse.setMessage("Store customers gotten successfully");
            getCustomerResponse.setCustomer(customerToGet);
            return getCustomerResponse;
        } catch (Exception e) {
            getCustomerResponse.setStatusCode(500);
            getCustomerResponse.setMessage("Invalid request");
        }
        return getCustomerResponse;
    }

    @Override
    public GetCustomerResponse getCustomerByEmailAddress(String emailAddress) {
        GetCustomerResponse getCustomerResponse = new GetCustomerResponse();
        try {
            Optional<Customer> customerToFind = customerRepository.findCustomerByEmail(emailAddress);
            if (customerToFind.isEmpty()) {
                getCustomerResponse.setStatusCode(400);
                getCustomerResponse.setMessage("No customer with email " + emailAddress + "found");
                return getCustomerResponse;
            }
            Customer customerToGet = customerToFind.get();
            getCustomerResponse.setStatusCode(200);
            getCustomerResponse.setMessage("Customer found successfully");
            getCustomerResponse.setCustomer(customerToGet);
            return getCustomerResponse;
        } catch (Exception e) {
            getCustomerResponse.setStatusCode(404);
            getCustomerResponse.setMessage("Invalid request");
        }
        return getCustomerResponse;
    }

    @Override
    public UpdateCustomerResponse updateCustomer(CreateCustomerRequest createCustomerRequest,String emailAddress) {
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        try {
            Optional<Customer> customer = customerRepository.findCustomerByEmail(emailAddress);
            if(customer.isEmpty()) {
                updateCustomerResponse.setStatusCode(400);
                updateCustomerResponse.setMessage("Customer not found");
                return updateCustomerResponse;
            }
           Customer customerToUpdate = Customer.builder()
                   .name(createCustomerRequest.getName())
                   .role(Role.ADMIN)
                   .userName(createCustomerRequest.getUserName())
                   .UUID(UUID.randomUUID().toString())
                   .password(passwordEncoder.encode(customer.get().getPassword()))
                   .review(customer.get().getReview())
                   .email(createCustomerRequest.getEmail())
                   .build();
            customerRepository.save(customerToUpdate);
            updateCustomerResponse.setStatusCode(200);
            updateCustomerResponse.setMessage("Customer updated successfully!!");
            return updateCustomerResponse;
        } catch (Exception e) {
            updateCustomerResponse.setStatusCode(404);
            updateCustomerResponse.setMessage("Invalid input for customer");
        }
        return updateCustomerResponse;
    }

    @Override
    public DeleteCustomerResponse deleteCustomer(Long id) {
        DeleteCustomerResponse deleteCustomerResponse = new DeleteCustomerResponse();
        try {
            Optional<Customer> customer = customerRepository.findById(id);
            if(customer.isEmpty()) {
                deleteCustomerResponse.setStatusCode(400);
                deleteCustomerResponse.setMessage("No customer found!!");
                return deleteCustomerResponse;
            }
            Customer customerToDelete = customer.get();
            customerRepository.delete(customerToDelete);
            return deleteCustomerResponse;
        } catch (Exception e) {
            deleteCustomerResponse.setStatusCode(404);
            deleteCustomerResponse.setMessage("Invalid action");
        }
        return deleteCustomerResponse;
    }

    @Override
    public GetCustomerResponse getAllOrders() {
        return null;
    }

    @Override
    public UpdateCustomerResponse addReview(Long productId) {
        return null;
    }
}
