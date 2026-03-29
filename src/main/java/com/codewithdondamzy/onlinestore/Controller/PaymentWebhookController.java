package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/OnlineStore/webhook")
public class PaymentWebhookController {
    private final PaymentService paymentService;
    public PaymentWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/paystack")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        paymentService.verifyPayment(payload);
        return ResponseEntity.ok("Success");
    }
}
