package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.ReviewRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.ReviewResponse;

public interface ReviewService {
    ReviewResponse updateReview(ReviewRequest reviewRequest, Long reviewId);
    ReviewResponse createReview(ReviewRequest reviewRequest);
    ReviewResponse getReview(Long reviewId);
    ReviewResponse deleteReview(Long reviewId);

}
