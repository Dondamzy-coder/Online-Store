package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.PaymentRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.PaymentResponse;
import com.codewithdondamzy.onlinestore.Service.PaymentService;
import com.codewithdondamzy.onlinestore.Service.PaystackPaymentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/OnlineStore")
public class PaymentController {
    private final PaymentService paymentService;

    private final PaystackPaymentService paystackPaymentService;

    public PaymentController(@Qualifier("paystackPaymentService") PaymentService paymentService, PaystackPaymentService paystackPaymentService) {
        this.paymentService = paymentService;
        this.paystackPaymentService = paystackPaymentService;
    }

    @PostMapping("/initializePayment/{orderId}")
    public ResponseEntity<?> initializePayment(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.initializePayment(orderId));
    }


    @GetMapping("/createPayment")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) {
        return  ResponseEntity.ok(paymentService.createPayment(paymentRequest));
    }
    public ResponseEntity<PaymentResponse> paymentSuccess(@RequestParam String reference) {
        return  ResponseEntity.ok(paymentService.paymentSuccess(reference));
    }
}
