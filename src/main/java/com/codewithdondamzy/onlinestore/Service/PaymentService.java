package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.PaymentRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.PaymentResponse;

import java.util.Map;

public interface PaymentService {

   PaymentResponse initializePayment(Long orderId);

    PaymentResponse verifyPayment(Map<String, Object> payload);

    PaymentResponse createPayment (PaymentRequest paymentRequest);

    PaymentResponse paymentSuccess(String reference);
}
