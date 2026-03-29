package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Request.PaymentRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.PaymentResponse;
import com.codewithdondamzy.onlinestore.Models.Order;
import com.codewithdondamzy.onlinestore.Models.Payment;
import com.codewithdondamzy.onlinestore.Models.PaymentMethod;
import com.codewithdondamzy.onlinestore.Models.PaymentStatus;
import com.codewithdondamzy.onlinestore.Repository.OrderRepository;
import com.codewithdondamzy.onlinestore.Repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StripePaymentService implements PaymentService {
    private OrderRepository orderRepository;
    private PaymentRepository paymentRepository;
    public StripePaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentResponse initializePayment(Long orderId) {
        PaymentResponse paymentResponse = new PaymentResponse();
        Order order = orderRepository.findOrderById(orderId).orElseThrow(() -> new RuntimeException("Order Not Found"));
        return null;
    }

    @Override
    public PaymentResponse verifyPayment(Map<String, Object> payload) {
        return null;
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        Payment payment = new Payment();
        payment.setAmount(paymentRequest.getAmount());
        payment.setDescription(paymentRequest.getDescription());
        payment.setReferenceNumber(paymentRequest.getReferenceNumber());
        payment.setStatus(paymentRequest.getStatus());
        payment.setMethod(paymentRequest.getMethod());
        paymentRepository.save(payment);
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setDescription(paymentRequest.getDescription());
        paymentResponse.setReferenceNumber(paymentRequest.getReferenceNumber());
        paymentResponse.setStatus(paymentRequest.getStatus());
        return paymentResponse;
    }

    @Override
    public PaymentResponse paymentSuccess(String reference) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentRepository.findPaymentByReferenceNumber(reference);
        try {
            if(!(paymentResponse.getStatus() == PaymentStatus.SUCCESS)) {
                paymentResponse.setStatus(PaymentStatus.FAILED);
                paymentResponse.setDescription(null);
                paymentResponse.setReferenceNumber(null);
                paymentResponse.setMethod(null);
            } else {
                paymentResponse.setStatus(PaymentStatus.SUCCESS);
                paymentResponse.setDescription("Success");
                paymentResponse.setReferenceNumber(reference);
                paymentResponse.setMethod(paymentResponse.getMethod());
            }
            return paymentResponse;
        } catch (Exception e) {
            paymentResponse.setMethod(PaymentMethod.INVALID);
            paymentResponse.setStatus(PaymentStatus.FAILED);
        }
        return paymentResponse;
    }
}
