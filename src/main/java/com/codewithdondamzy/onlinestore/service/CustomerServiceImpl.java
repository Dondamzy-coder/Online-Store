package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Request.*;
import com.codewithdondamzy.onlinestore.Dtos.Response.*;
import com.codewithdondamzy.onlinestore.Models.*;
import com.codewithdondamzy.onlinestore.Repository.*;
import com.codewithdondamzy.onlinestore.config.JwtService;
import com.codewithdondamzy.onlinestore.config.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final JwtService jwtService;

    public CustomerServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder,
                               AddressRepository addressRepository, ProductRepository productRepository,
                               CartRepository cartRepository, CartItemRepository cartItemRepository,
                               OrderRepository orderRepository, ReviewRepository reviewRepository,
                               AuthenticationManager authenticationManager, JwtService jwtService) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
                    .phoneNumber(createCustomerRequest.getPhoneNumber())
                    .password(passwordEncoder.encode(createCustomerRequest.getPassword()))
                    .createdAt(LocalDate.now())
                    .role(createCustomerRequest.getRole())
                    .UUID(UUID.randomUUID().toString())
                    .isActive(true)
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
            creatCustomerResponse.setData(customer);

            return creatCustomerResponse;
        } catch (Exception e) {
            e.printStackTrace();
            creatCustomerResponse.setStatusCode(500);
            creatCustomerResponse.setMessage("Internal Server Error,please try again later!!");
            return creatCustomerResponse;
        }
    }

    @Override
    public CreateCustomerResponse customerLogin(CreateCustomerLoginRequest createCustomerLoginRequest) {
        CreateCustomerResponse createCustomerResponse = new CreateCustomerResponse();
        try {
            // 🔐 Authenticate credentials using Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            createCustomerLoginRequest.getUserName(),
                            createCustomerLoginRequest.getPassword()
                    )
            );
            Customer existingCustomer = customerRepository.findByUserName(createCustomerLoginRequest.getUserName());
            if (existingCustomer == null) {
                createCustomerResponse.setStatusCode(401);
                createCustomerResponse.setMessage("Customer with username " + createCustomerLoginRequest.getUserName() + " not found");
                return createCustomerResponse;
            }

            System.out.println("Raw password: " + createCustomerLoginRequest.getPassword());
            System.out.println("DB password: " + existingCustomer.getPassword());
            System.out.println("Matches: " + passwordEncoder.matches(
                    createCustomerLoginRequest.getPassword(),
                    existingCustomer.getPassword()
            ));

            String jwtToken = jwtService.generateToken(new UserPrincipal(existingCustomer));
            createCustomerResponse.setJwtToken(jwtToken);
            createCustomerResponse.setStatusCode(200);
            createCustomerResponse.setMessage("Customer login successfully!!");
            return createCustomerResponse;
        } catch (IllegalArgumentException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            createCustomerResponse.setStatusCode(500);
            createCustomerResponse.setMessage("Invalid input try again!!");
            return createCustomerResponse;
        }
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
        String userName = authentication.getName();
        try {
            Optional<Customer> customer = Optional.ofNullable(customerRepository.findByUserName(userName));
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
    @Transactional
    public UpdateCustomerResponse addItemToCart(Long productId, Long cartId, int quantity) {
        UpdateCustomerResponse response = new UpdateCustomerResponse();
        // 1. Validate product
        try {
            Optional<Products> product = productRepository.findById(productId);
            if(product.isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("Product is not available");
                return response;
            }
            Products productToAdd = product.get();
            // 2. Validate cart
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
//              check product stock against quantity
            if(productToAdd.getInventory() < quantity) {
                response.setStatusCode(400);
                response.setMessage("Oops!!! Product is out of stock");
                return response;
            }

            // 3. Check if products already exist in carts
            Optional<CartItem> existingItem =
                    cartItemRepository.findByCartAndProduct(cart, Optional.of(productToAdd));

            if (existingItem.isPresent()) {
                // Update quantity
                CartItem cartItem = existingItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItem.calculateTotalPrice();
                cartItemRepository.save(cartItem);
                cartRepository.save(cart);
            } else {
                // Create new cart items
                    CartItem cartItem = new CartItem();
                    cartItem.setCart(cart);
                    cartItem.setProduct(productToAdd);
                    cartItem.setQuantity(quantity);
                    cartItem.setUnitPrice(productToAdd.getPrice());
                    cartItem.calculateTotalPrice();
                    cartItemRepository.save(cartItem);
                    cart.addItem(cartItem);
            }
            response.setStatusCode(200);
            response.setMessage("Product added to cart successfully");
            return response;
        } catch (RuntimeException e) {
            response.setStatusCode(500);
            response.setMessage("Unable to add product to cart");
            return response;
        }
    }

    @Override
    public GetCustomerResponse getAllCustomers(Pageable pageable) {
        GetCustomerResponse getCustomerResponse = new GetCustomerResponse();
        try {

            Page<Customer> customersPage = customerRepository.findAll(pageable);
            List<Customer> customerList = customersPage.getContent();
            if (customerList.isEmpty()) {
                getCustomerResponse.setStatusCode(400);
                getCustomerResponse.setMessage("No customer found!!");
                return getCustomerResponse;
            }
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", customerList);
            responseData.put("totalPages", customersPage.getTotalPages());
            responseData.put("totalElements", customersPage.getTotalElements());
            responseData.put("currentPage", customersPage.getNumber());
            responseData.put("pageSize", customersPage.getSize());

            getCustomerResponse.setStatusCode(200);
            getCustomerResponse.setMessage("Store customers gotten successfully");
            getCustomerResponse.setCustomer(customerList);
            getCustomerResponse.setData(responseData);
            return getCustomerResponse;

        }
        catch (Exception e) {
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
            return getCustomerResponse;
        } catch (Exception e) {
            getCustomerResponse.setStatusCode(404);
            getCustomerResponse.setMessage("Invalid request");
        }
        return getCustomerResponse;
    }

    @Override
    public UpdateCustomerResponse updateCustomerById(CreateCustomerRequest createCustomerRequest, Long id) {
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        try {
            Optional<Customer> customer = customerRepository.findById(id);
            if (customer.isEmpty()) {
                updateCustomerResponse.setStatusCode(400);
                updateCustomerResponse.setMessage("Customer not found");
                return updateCustomerResponse;
            }
            Customer customerToUpdate = Customer.builder()
                    .name(createCustomerRequest.getName())
                    .role(createCustomerRequest.getRole())
                    .userName(createCustomerRequest.getUserName())
                    .UUID(UUID.randomUUID().toString())
                    .password(passwordEncoder.encode(customer.get().getPassword()))
                    .review(new ArrayList<>(customer.get().getReview()))
                    .email(createCustomerRequest.getEmail())
                    .isActive(true)
                    .createdAt(LocalDate.now())
                    .build();
            customerRepository.save(customerToUpdate);
            updateCustomerResponse.setStatusCode(200);
            updateCustomerResponse.setMessage("Customer updated successfully!!");
            return updateCustomerResponse;
        } catch (Exception e) {
            e.printStackTrace();
            updateCustomerResponse.setStatusCode(500);
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
    public GetCustomerResponse getAllOrders(Pageable pageable) {
        GetCustomerResponse getCustomerResponse = new GetCustomerResponse();
        try {
            Page<Order> orderPage = orderRepository.findAll(pageable);
            List<Order> orderList = orderPage.getContent();
            if (orderList.isEmpty()) {
                getCustomerResponse.setStatusCode(400);
                getCustomerResponse.setMessage("No orders found!");
                return getCustomerResponse;
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", orderList);
            responseData.put("totalPages", orderPage.getTotalPages());
            responseData.put("totalElements", orderPage.getTotalElements());
            responseData.put("currentPage", orderPage.getNumber());
            responseData.put("pageSize", orderPage.getSize());
            responseData.put("numberOfElements", orderPage.getNumberOfElements());

            getCustomerResponse.setStatusCode(200);
            getCustomerResponse.setMessage("Orders found successfully");
            getCustomerResponse.setData(orderList);
            return getCustomerResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ReviewResponse addReviewToProduct(Authentication authentication, ReviewRequest reviewRequest, String productName) {
        ReviewResponse reviewResponse = new ReviewResponse();
        try {
            String userName = authentication.getName();
            Customer customer  = customerRepository.findByUserName(userName);
            if(!customer.getUserName().equals(userName)) {
                reviewResponse.setStatusCode(400);
                reviewResponse.setMessage("Customer not found, cannot add review");
                return reviewResponse;
            }
            Optional<Products> productToReview = productRepository.findByName(productName);
            if(productToReview.isEmpty()) {
                reviewResponse.setStatusCode(400);
                reviewResponse.setMessage("Product not found, cannot add review");
                return reviewResponse;
            }
            List<Review> review = Collections.singletonList(Review.builder()
                    .product(productToReview.get())
                    .customer(customer)
                    .UUID(UUID.randomUUID().toString())
                    .rating(reviewRequest.getRating())
                    .createdAt(LocalDateTime.now())
                    .comment(reviewRequest.getComment())
                    .build());
            productToReview.get().setReviews(review);
            reviewRepository.saveAll(review);
            reviewResponse.setStatusCode(200);
            reviewResponse.setMessage("Review added successfully");
            return reviewResponse;
        } catch (Exception e) {
            reviewResponse.setStatusCode(400);
            reviewResponse.setMessage("Invalid request");
            return reviewResponse;
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
