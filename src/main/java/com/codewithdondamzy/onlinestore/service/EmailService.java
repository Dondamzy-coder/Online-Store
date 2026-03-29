package com.codewithdondamzy.onlinestore.service;

import jakarta.mail.MessagingException;

import java.math.BigDecimal;

public interface EmailService {
    void sendEmailForSuccessfulOrder(String customerEmail, String customerName, Long orderId) throws Exception;

    void sendPaymentConfirmation(String customerEmail, String customerName, Long orderId, String paymentReference, BigDecimal amount) throws MessagingException;

}
