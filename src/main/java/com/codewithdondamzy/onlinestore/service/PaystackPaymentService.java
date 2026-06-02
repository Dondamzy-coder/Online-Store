package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Request.PaymentRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.PaymentResponse;
import com.codewithdondamzy.onlinestore.Models.*;
import com.codewithdondamzy.onlinestore.Repository.OrderRepository;
import com.codewithdondamzy.onlinestore.Repository.PaymentRepository;
import com.codewithdondamzy.onlinestore.Repository.ProductRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Primary
public class PaystackPaymentService implements PaymentService {
    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository; // for fetching order from database

    private final RestTemplate restTemplate; // To make API calls to paystack
    private final ProductRepository productRepository;
    private final EmailService emailService;

    public PaystackPaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, RestTemplate restTemplate, ProductRepository productRepository, EmailService emailService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.productRepository = productRepository;
        this.emailService = emailService;
    }

    // secret key from application-dev.properties
    @Value("${paystack.secret.key}")
    private String secretKey;

    @Override
    public PaymentResponse initializePayment(Long orderId) {
        PaymentResponse paymentResponse = new PaymentResponse();

        Order order = orderRepository.findOrderById(orderId).orElseThrow(()->
                new RuntimeException("Order Not Found"));

        // Get amount from order
        BigDecimal totalAmount = order.getTotalPrice();

        // Convert to kobo
        long amountInKobo = totalAmount
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();

        // prepare Headers for Paystack API request
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(secretKey); // Authorize using secret key
        headers.setContentType(MediaType.APPLICATION_JSON); // send JSON BODY

        Map<String, Object> body = new HashMap<>(); // Prepare requests for paystack initialize endpoint
        body.put("email",order.getCustomer().getEmail());
        body.put("amount",amountInKobo); // convert to kobo

        // wrap header and body into HttpENTITY
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

//        Send POST request to paystack initialize  transaction endpoint
        ResponseEntity<Map> response = restTemplate.exchange("https://api.paystack.co/transaction/initialize",
                HttpMethod.POST, request, Map.class);

//        Extract data from paystack response
        Map data = (Map)response.getBody().get("data");

        String reference = (String)data.get("reference");

        String authorizationUrl = (String)data.get("authorization_url"); // url to redirect customer

        order.setPaymentReference(reference); //Save reference and set payment status to PENDING

        order.setPaymentStatus(PaymentStatus.PENDING);

        orderRepository.save(order);

        paymentResponse.setStatusCode(200);
        paymentResponse.setReferenceNumber(reference);
        paymentResponse.setAuthorizationUrl(authorizationUrl);
        paymentResponse.setDescription("Paystack Payment");
        paymentResponse.setStatus(PaymentStatus.PENDING);
        return paymentResponse; // contains reference and authorization url
    }

    @Override
    public PaymentResponse verifyPayment(Map<String, Object> payload) {
        PaymentResponse paymentResponse = new PaymentResponse();

//      Extract the 'data' object from webhook payload
        Map data = (Map) payload.get("data");

        String reference = (String) data.get("reference"); // get payment reference

//        find the order by payment reference
        Optional<Order> order = orderRepository.findByPaymentReference(reference);

//        check payment status from webhook
        if ("success".equals(data.get("status"))) {
            paymentResponse.setStatusCode(200);
            paymentResponse.setReferenceNumber(reference);
            paymentResponse.setStatus(PaymentStatus.SUCCESS);
            paymentResponse.setDescription("Payment successful");
            paymentResponse.setMethod(PaymentMethod.BANK_TRANSFER);

            Order order1 = order.get();
//            reduce product inventory after successful payment
            for (OrderItem orderItem : order1.getOrderItems()) {
                Products products = orderItem.getProducts();
                products.setInventory(products.getInventory() - orderItem.getQuantity());
                productRepository.save(products);
                order1.setPaymentStatus(paymentResponse.getStatus());
                orderRepository.save(order1);
                try {
                    emailService.sendPaymentConfirmation(order1.getCustomer().getEmail()
                            ,order1.getCustomer().getName(),
                            order1.getId(),
                            order1.getPaymentReference(),
                            order1.getTotalPrice());
                } catch (MessagingException e) {
                    paymentResponse.setStatusCode(500);
                    paymentResponse.setDescription("Paystack Payment Failed");
                    return paymentResponse;
                }
            }
        }
            paymentResponse.setStatus(PaymentStatus.FAILED);
            order.get().setPaymentStatus(PaymentStatus.FAILED);
            paymentResponse.setDescription("Payment failed");
            paymentResponse.setMethod(PaymentMethod.BANK_TRANSFER);
            return paymentResponse;
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = new PaymentResponse();
        Payment newPayment = new Payment();
        newPayment.setDescription(paymentRequest.getDescription());
        newPayment.setAmount(paymentRequest.getAmount());
        newPayment.setMethod(paymentRequest.getMethod());
        newPayment.setStatus(paymentRequest.getStatus());
        return paymentResponse;
    }

    @Override
    public PaymentResponse paymentSuccess(String reference) {
        PaymentResponse paymentResponse =new PaymentResponse();
        Payment payment = paymentRepository.findPaymentByReferenceNumber(paymentResponse.getReferenceNumber());
        if(!payment.getStatus().equals(PaymentStatus.SUCCESS)) {
            paymentResponse.setReferenceNumber(reference);
            paymentResponse.setStatus(PaymentStatus.PENDING);
            paymentResponse.setDescription("Processing payment");
            paymentResponse.setMethod(PaymentMethod.BANK_TRANSFER);
        } else {
            paymentResponse.setReferenceNumber(reference);
            paymentResponse.setStatus(PaymentStatus.SUCCESS);
            paymentResponse.setDescription("Payment successful");
            paymentResponse.setMethod(PaymentMethod.BANK_TRANSFER);
        }
        return paymentResponse;
    }
}
