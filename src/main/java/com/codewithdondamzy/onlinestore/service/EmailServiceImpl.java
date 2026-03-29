package com.codewithdondamzy.onlinestore.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailServiceImpl implements  EmailService {
    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmailForSuccessfulOrder(String customerEmail, String customerName, Long orderId) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setFrom("dondamzy99@gmail.com");
        messageHelper.setTo(customerEmail);
        String subject = "Successful Order";
        String content = "<p>Hello " + customerName + ",</p>"
                + "<p>You have successfully placed an order. Your goods will be delivered to you within " +
                "3 - 4 business working days</p>"
                + "<p>Here is your orderId</p>" + orderId
                + "<p>. Thank you for choosing Dondamzy Online Store!!!.</p>";
        messageHelper.setSubject(subject);
        messageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
    }

    @Override
    public void sendPaymentConfirmation(String customerEmail, String customerName, Long orderId, String paymentReference, BigDecimal amount) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setFrom("dondamzy99@gmail.com");
        messageHelper.setTo(customerEmail);
        String subject = "Payment Confirmation";
        String content = "Dear " + customerName + "," +
                "\n\n" +
                "Your payment was successful.\n\n" +
                "Order Id: " + orderId + "\n\n" +
                "Payment Reference: " + paymentReference + "\n\n" +
                "Amount paid: $" + amount + "\n\n" +
                "Your order is now being processed.\n\n" +
                "Thank you for shopping with us.";
        messageHelper.setSubject(subject);
        messageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
    }
}
