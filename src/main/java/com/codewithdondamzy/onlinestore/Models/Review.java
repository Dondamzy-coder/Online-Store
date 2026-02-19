package com.codewithdondamzy.onlinestore.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int rating;
    private String comment;
    private String UUID;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Products product;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference("customer-review")
    private Customer customer;

}
