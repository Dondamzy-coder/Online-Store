package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.PaymentRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment (PaymentRequest paymentRequest);

    PaymentResponse paymentSuccess(String reference);
}
