package com.codewithdondamzy.onlinestore.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dateCreated;
    private LocalDate dateShipped;
    private BigDecimal totalPrice;
    private String orderNumber;
    @OneToOne(mappedBy = "order",cascade = CascadeType.ALL)
    private ShippingInfo shippingInfo;

    private String paymentReference;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private String shippingId;
    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();
    @OneToOne(mappedBy = "order",cascade = CascadeType.ALL)
    private Payment payment;

    private PaymentStatus paymentStatus;
}
