package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.PaymentRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.PaymentResponse;
import com.codewithdondamzy.onlinestore.Models.Payment;
import com.codewithdondamzy.onlinestore.Models.PaymentMethod;
import com.codewithdondamzy.onlinestore.Models.PaymentStatus;
import com.codewithdondamzy.onlinestore.Repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaypalPaymentService implements PaymentService {
    private final PaymentRepository paymentRepository;

    public PaypalPaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
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
