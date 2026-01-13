package com.codewithdondamzy.onlinestore.Dtos.Request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReviewRequest {
    private int rating;
    private String comment;
    private LocalDate createdAt;
    private CreateProductRequest createProductRequest;
    private CreateCustomerRequest createCustomerRequest;
}
