package com.codewithdondamzy.onlinestore.Service;


import com.codewithdondamzy.onlinestore.Dtos.Request.OrderRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.OrderResponse;
import com.codewithdondamzy.onlinestore.Models.*;
import com.codewithdondamzy.onlinestore.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderItemsRepository orderItemsRepository;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    public OrderServiceImpl(PaymentService paymentService, CartRepository cartRepository, ProductRepository productRepository, CartService cartService, OrderItemsRepository orderItemsRepository, OrderRepository orderRepository, PaymentRepository paymentRepository, EmailService emailService) {
        this.paymentService = paymentService;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.orderItemsRepository = orderItemsRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.emailService = emailService;
    }

    private List<OrderItem> createOrderItems(Order order,Cart cart) {
        List<OrderItem> orderItemList = new ArrayList<>();
        if (cart.getCartItem() != null && !cart.getCartItem().isEmpty()) {
            for (CartItem cartItem : cart.getCartItem()) {
                Products products = cartItem.getProduct();
                if (products.getInventory() < cartItem.getQuantity()) {
                    throw new RuntimeException("Insufficient inventory for " + products.getName());
                }
                products.setInventory(products.getInventory() - cartItem.getQuantity());
                productRepository.save(products);
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProducts(products);
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(products.getPrice());
                orderItemList.add(orderItem);
            }
        }
        return orderItemList;
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PENDING);
        order.setDateCreated(LocalDate.now());
        order.setCustomer(cart.getCustomer());
        order.setShippingInfo(new ShippingInfo());
        order.setTotalPrice(cart.getTotalPrice());
        order.setDateShipped(null);
        order.setShippingId(UUID.randomUUID().toString());
        order.setTotalPrice(order.getTotalPrice());
        order.setOrderItems(new HashSet<>(orderItemsRepository.findAll()));
        orderRepository.save(order);
        return order;
    }

    @Transactional
    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest, Long customerId) {
        OrderResponse orderResponse = new OrderResponse();
        try {
            Cart cart = cartRepository.getCartByCustomer_Id(customerId);
            if (cart == null) {
                orderResponse.setStatusCode(404);
                orderResponse.setMessage("Cart not found for customer");
                return orderResponse;
            }

            if(!cart.getCustomer().isActive()) {
                orderResponse.setStatusCode(401);
                orderResponse.setMessage("Customer is not active to place order");
                return orderResponse;
            }
            Order order = createOrder(cart);

            List<OrderItem> orderItems = createOrderItems(order,cart);

            order.setOrderItems(new HashSet<>(orderItems));
            order.setTotalPrice(calculateTotalPrice(orderItems));
            Order savedOrder = orderRepository.save(order);
            emailService.sendEmailForSuccessfulOrder(order.getCustomer().getEmail()
                    ,order.getCustomer().getName(),order.getId());
            cartService.clearCartById(cart.getId());

            Payment payment = Payment.builder()
                    .order(savedOrder)
                    .amount(savedOrder.getTotalPrice())
                    .method(PaymentMethod.BANK_TRANSFER)
                    .status(PaymentStatus.PENDING)
                    .referenceNumber(UUID.randomUUID().toString())
                    .date(LocalDate.now())
                    .build();

            paymentRepository.save(payment);

            payment.setReferenceNumber(UUID.randomUUID().toString());
            orderResponse.setStatusCode(200);
            orderResponse.setMessage("Order placed successfully");
            orderResponse.setTotalAmount(savedOrder.getTotalPrice());
            orderResponse.setOrderId(customerId);
            orderResponse.setOrderId(savedOrder.getId());
            return orderResponse;
        } catch (Exception e) {
            e.printStackTrace();
            orderResponse.setStatusCode(500);
            orderResponse.setMessage(e.getMessage());
            return orderResponse;
        }
    }


    private BigDecimal calculateTotalPrice(List<OrderItem> orderItems) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        if(orderItems != null) {
            for (OrderItem orderItem : orderItems) {
                orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                totalPrice = totalPrice.add(orderItem.getPrice());
                break;
            }
        }
        return totalPrice;
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        OrderResponse orderResponse = new OrderResponse();
        try {
            Optional<Order> order = orderRepository.findOrderById(orderId);
            if(order.isPresent()) {
                orderResponse.setStatusCode(200);
                orderResponse.setMessage("Order exists and gotten Successfully!");
                return orderResponse;
            }
            orderResponse.setStatusCode(400);
            orderResponse.setMessage("Order Not Found");
            return orderResponse;
        } catch (Exception e) {
            orderResponse.setStatusCode(500);
            orderResponse.setMessage("Internal server error,please try again later!!");
            return orderResponse;
        }
    }

    @Override
    public OrderResponse deleteOrderById(Long orderId) {
        OrderResponse orderResponse  = new OrderResponse();
        try {
            Optional<Order> order = orderRepository.findOrderById(orderId);
            if(order.isEmpty()) {
                orderResponse.setStatusCode(404);
                orderResponse.setMessage("Order Not Found");
                return orderResponse;
            }
            Order orderToDelete = order.get();
            orderRepository.delete(orderToDelete);
            orderResponse.setStatusCode(200);
            orderResponse.setMessage("Order deleted successfully!");
            orderResponse.setData(String.valueOf(orderToDelete));
            return orderResponse;
        } catch (Exception e) {
            orderResponse.setStatusCode(500);
            orderResponse.setOrderId(orderId);
            orderResponse.setMessage("Internal server error,please try again later!!");
            return orderResponse;
        }
    }

    @Override
    public OrderResponse getAllCustomerOrder(Long customerId) {
        OrderResponse orderResponse = new OrderResponse();
        try {
            List<Order> order = orderRepository.findOrderByCustomer_Id(customerId);
            if(order.isEmpty()) {
                orderResponse.setStatusCode(400);
                orderResponse.setMessage("Order Not Found,customer has no order");
                return orderResponse;
            }
            List<Order> orderList = new ArrayList<>(order);
            orderResponse.setStatusCode(200);
            orderResponse.setMessage("user Orders gotten Successfully");
            orderResponse.setData(orderList.toString());
            return orderResponse;
        } catch (Exception e) {
            orderResponse.setStatusCode(500);
            orderResponse.setMessage("Internal server error,please try again later!!");
            return orderResponse;
        }
    }
}
