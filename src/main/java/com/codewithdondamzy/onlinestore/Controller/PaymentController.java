package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.PaymentRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.PaymentResponse;
import com.codewithdondamzy.onlinestore.Service.PaymentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/OnlineStore")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(@Qualifier("paypalPaymentService") PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/createPayment")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) {
        return  ResponseEntity.ok(paymentService.createPayment(paymentRequest));
    }
    public ResponseEntity<PaymentResponse> paymentSuccess(@RequestParam String reference) {
        return  ResponseEntity.ok(paymentService.paymentSuccess(reference));
    }
}
