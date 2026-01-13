package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.AddressRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.ChangePasswordRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerLoginRequest;
import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.CreateCustomerResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.DeleteCustomerResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.GetCustomerResponse;
import com.codewithdondamzy.onlinestore.Dtos.Response.UpdateCustomerResponse;
import com.codewithdondamzy.onlinestore.Models.*;
import com.codewithdondamzy.onlinestore.Repository.*;
import com.codewithdondamzy.onlinestore.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public CustomerServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder,
                               AddressRepository addressRepository, ProductRepository productRepository,
                               CartRepository cartRepository, CartItemRepository cartItemRepository, OrderRepository orderRepository, ReviewRepository reviewRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }


    @Override
    public CreateCustomerResponse createCustomer(CreateCustomerRequest createCustomerRequest) {
        CreateCustomerResponse creatCustomerResponse = new CreateCustomerResponse();
        try {
            Optional<Customer> newCustomer = customerRepository.findCustomerByEmail(createCustomerRequest.getEmail());
            if (newCustomer.isPresent()) {
                creatCustomerResponse.setStatusCode(400);
                creatCustomerResponse.setMessage("Customer already exists");
                return creatCustomerResponse;
            }
            Customer customer = Customer.builder()
                    .name(createCustomerRequest.getName())
                    .email(createCustomerRequest.getEmail())
                    .userName(createCustomerRequest.getUserName())
                    .password(createCustomerRequest.getPassword())
                    .createdAt(LocalDate.now())
                    .role(Role.CUSTOMER)
                    .UUID(UUID.randomUUID().toString())
                    .build();
            // Create cart
            Cart cart = Cart.builder()
                    .cartItem(new ArrayList<>())
                    .totalPrice(BigDecimal.ZERO)
                    .customer(customer)
                    .build();
            customer.setCart(cart);

            List<Address> addresses = new ArrayList<>();
            for(AddressRequest addressRequest : createCustomerRequest.getAddress()) {
                Address address = Address.builder()
                        .city(addressRequest.getCity())
                        .country(addressRequest.getCountry())
                        .street(addressRequest.getStreet())
                        .zipCode(addressRequest.getZipCode())
                        .customer(customer)
                        .build();
                addresses.add(address);
            }
            customer.setAddresses(addresses);
            // Initialize empty orders
            customer.setOrders(new ArrayList<>());
            customerRepository.save(customer);
            creatCustomerResponse.setStatusCode(200);
            creatCustomerResponse.setMessage("Customer created successfully!!");

            return creatCustomerResponse;
        } catch (Exception e) {
            e.printStackTrace();
            creatCustomerResponse.setStatusCode(500);
            creatCustomerResponse.setMessage("Internal Server Error,please try again later!!");
        }
        return creatCustomerResponse;
    }

    @Override
    public CreateCustomerResponse customerLogin(CreateCustomerLoginRequest createCustomerLoginRequest) {
        CreateCustomerResponse createCustomerResponse = new CreateCustomerResponse();
        try {
            Customer existingCustomer = customerRepository.findByUserName(createCustomerLoginRequest.getUserName());
            if(existingCustomer == null) {
                createCustomerResponse.setStatusCode(401);
                createCustomerResponse.setMessage("Customer with username " + createCustomerLoginRequest.getUserName() + " not found");
                return createCustomerResponse;
            }
            // 🔐 Authenticate credentials using Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            createCustomerLoginRequest.getUserName(),
                            createCustomerLoginRequest.getPassword()
                    )
            );
            String jwtToken = jwtUtils.generateJwtToken(new UserPrincipal(existingCustomer));
            createCustomerResponse.setJwtToken(jwtToken);
            createCustomerResponse.setStatusCode(200);
            createCustomerResponse.setMessage("Customer login successfully!!");
            return createCustomerResponse;
        } catch (IllegalArgumentException e) {
            createCustomerResponse.setStatusCode(500);
            createCustomerResponse.setMessage("Invalid input try again!!");
        }
        return createCustomerResponse;
    }

    @Override
    public UpdateCustomerResponse changePassword(ChangePasswordRequest changePasswordRequest, Authentication authentication) {
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        String userName = authentication.getName();
        try {
            Optional<Customer> customer = Optional.ofNullable(customerRepository.findByUserName(userName));
            if (customer.isEmpty()) {
                updateCustomerResponse.setStatusCode(402);
                updateCustomerResponse.setMessage("Customer not found!");
                return updateCustomerResponse;
            }
            Customer existingCustomer = customer.get();
            if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), existingCustomer.getPassword())) {
                updateCustomerResponse.setStatusCode(403);
                updateCustomerResponse.setMessage("Old password doesn't match!");
                return updateCustomerResponse;
            }
            if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), existingCustomer.getPassword())) {
                updateCustomerResponse.setStatusCode(401);
                updateCustomerResponse.setMessage("New password must be different from the old password!");
                return updateCustomerResponse;
            }
            existingCustomer.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            customerRepository.save(existingCustomer);
            updateCustomerResponse.setStatusCode(200);
            updateCustomerResponse.setMessage("Customer password updated successfully!!");
            updateCustomerResponse.setData(customer);
            return updateCustomerResponse;
        } catch (Exception e) {
            e.printStackTrace();
            updateCustomerResponse.setStatusCode(500);
            updateCustomerResponse.setMessage("Unexpected error occurred!!");
            return updateCustomerResponse;
        }
    }

    @Override
    public UpdateCustomerResponse setAddressAsDefault(Long addressId, Authentication authentication) {
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
            cartItem.calculateTotalPrice();
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
            if (customerList.isEmpty()) {
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
    public GetCustomerResponse getCustomerByEmailAddress(Authentication authentication) {
        GetCustomerResponse getCustomerResponse = new GetCustomerResponse();
        try {
            String emailAddress = authentication.getName();
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
    public UpdateCustomerResponse updateCustomer(CreateCustomerRequest createCustomerRequest, Authentication authentication) {
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        try {
            String emailAddress = authentication.getName();
            ;
            Optional<Customer> customer = customerRepository.findCustomerByEmail(emailAddress);
            if (customer.isEmpty()) {
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
            if (customer.isEmpty()) {
                deleteCustomerResponse.setStatusCode(400);
                deleteCustomerResponse.setMessage("No customer found!!");
                return deleteCustomerResponse;
            }
            Customer customerToDelete = customer.get();
            customerRepository.delete(customerToDelete);
            deleteCustomerResponse.setStatusCode(200);
            deleteCustomerResponse.setMessage("Customer deleted successfully!!");
            return deleteCustomerResponse;
        } catch (Exception e) {
            deleteCustomerResponse.setStatusCode(404);
            deleteCustomerResponse.setMessage("Invalid action");
        }
        return deleteCustomerResponse;
    }

    @Override
    public GetCustomerResponse getAllOrders() {
        GetCustomerResponse getCustomerResponse = new GetCustomerResponse();
        List<Order> orderList = orderRepository.findAll();
        if (orderList.isEmpty()) {
            getCustomerResponse.setStatusCode(400);
            getCustomerResponse.setMessage("No orders found!");
            return getCustomerResponse;
        }
        List<Order> allOrders = new ArrayList<>(orderList);
        getCustomerResponse.setStatusCode(200);
        getCustomerResponse.setMessage("Orders found successfully");
        getCustomerResponse.setData(allOrders);
        return getCustomerResponse;
    }

    @Override
    public UpdateCustomerResponse addReview(Long reviewId, Long productId) {
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        try {
            Optional<Products> products = productRepository.findProductsById(productId);
            List<Review> reviews = reviewRepository.findByProductId(productId);
            if (products.isEmpty()) {
                updateCustomerResponse.setStatusCode(400);
                updateCustomerResponse.setMessage("No product found!");
                return updateCustomerResponse;
            }
            if (reviews.isEmpty()) {
                updateCustomerResponse.setStatusCode(400);
                updateCustomerResponse.setMessage("No review found!");
                return updateCustomerResponse;
            }
            List<Review> allReviews = new ArrayList<>(reviews);
            allReviews.addAll(reviews);
            Products productToReview = products.get();
            productToReview.setReviews(reviews);
            updateCustomerResponse.setStatusCode(200);
            updateCustomerResponse.setMessage("Review added to product successfully");
            updateCustomerResponse.setData(productToReview);
            return updateCustomerResponse;
        } catch (Exception e) {
            updateCustomerResponse.setStatusCode(500);
            updateCustomerResponse.setMessage("Error adding review");
            return updateCustomerResponse;
        }
    }

    @Override
    public UpdateCustomerResponse updateReview(Long productId) {
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        try {
            Optional<Products> products = productRepository.findProductsById(productId);
            if (products.isEmpty()) {
                updateCustomerResponse.setStatusCode(400);
                updateCustomerResponse.setMessage("No product found!");
                return updateCustomerResponse;
            }
            Products productToUpdate = products.get();
            productToUpdate.setReviews(reviewRepository.findByProductId(productId));
            updateCustomerResponse.setStatusCode(200);
            updateCustomerResponse.setMessage("Product Review updated successfully");
            return updateCustomerResponse;
        } catch (Exception e) {
            updateCustomerResponse.setStatusCode(500);
            updateCustomerResponse.setMessage("Error updating product review");
            return updateCustomerResponse;
        }
    }

    @Override
    public UpdateCustomerResponse manageCustomer(String emailAddress, Authentication authentication) {
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        String userName = authentication.getName();
        try {
            Optional<Customer> customer = customerRepository.findCustomerByEmail(emailAddress);
            if (customer.isEmpty()) {
                updateCustomerResponse.setStatusCode(400);
                updateCustomerResponse.setMessage("No customer found!");
                return updateCustomerResponse;
            }
            if (customer.get().getUserName().equals(userName)) {
                updateCustomerResponse.setStatusCode(402);
                updateCustomerResponse.setMessage("Sorry you cannot manage urself!!");
                return updateCustomerResponse;
            }
            Customer customerToManage = customer.get();
            if (!customerToManage.getRole().equals(Role.ADMIN) && !customerToManage.getRole().equals(Role.CUSTOMER)) {
                updateCustomerResponse.setStatusCode(401);
                updateCustomerResponse.setMessage("This user is a new guest to the store");
                return updateCustomerResponse;
            }
            customerToManage.setRole(Role.GUEST);
            customerRepository.save(customerToManage);
            updateCustomerResponse.setStatusCode(200);
            updateCustomerResponse.setMessage("Customer managed successfully");
            updateCustomerResponse.setData(customerToManage);
            return updateCustomerResponse;
        } catch (Exception e) {
            updateCustomerResponse.setStatusCode(500);
            updateCustomerResponse.setMessage("There was a problem managing customer,please try again later");
            return updateCustomerResponse;
        }
    }
}
