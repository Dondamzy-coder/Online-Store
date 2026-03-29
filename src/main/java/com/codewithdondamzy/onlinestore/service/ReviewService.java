package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Request.ReviewRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.ReviewResponse;
import org.springframework.security.core.Authentication;

public interface ReviewService {
    ReviewResponse addReviewToProduct(Authentication authentication, ReviewRequest reviewRequest, String productName);

    ReviewResponse updateReview(ReviewRequest reviewRequest, Long reviewId);

    ReviewResponse getReview(Long reviewId);

    ReviewResponse deleteReview(Long reviewId);



}
