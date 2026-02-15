package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.ReviewRequest;
import com.codewithdondamzy.onlinestore.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/OnlineStore")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PostMapping("/addReviewToProducts")
    public ResponseEntity<?> addReviewToProducts(Authentication authentication, @RequestBody ReviewRequest reviewRequest,
                                                 @RequestParam String productName) {
        return ResponseEntity.ok(reviewService.addReviewToProduct(authentication, reviewRequest, productName));
    }

    @PutMapping("/updateReview/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok(reviewService.updateReview(reviewRequest, reviewId));
    }


    @GetMapping("/getReviewById/{reviewId}")
    public ResponseEntity<?> getReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }


    @DeleteMapping("/deleteProductReview/{reviewId}")
    public ResponseEntity<?> deleteProductReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId));
    }
}
