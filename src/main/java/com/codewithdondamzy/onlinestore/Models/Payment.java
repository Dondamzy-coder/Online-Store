package com.codewithdondamzy.onlinestore.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String referenceNumber;
    private BigDecimal amount;
    @Column(length = 10)
    private int item;
    @Column(length = 255)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;


}
