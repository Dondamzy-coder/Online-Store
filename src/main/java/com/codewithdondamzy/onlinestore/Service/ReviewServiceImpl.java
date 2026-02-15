package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.ReviewRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.ReviewResponse;
import com.codewithdondamzy.onlinestore.Models.Customer;
import com.codewithdondamzy.onlinestore.Models.Products;
import com.codewithdondamzy.onlinestore.Models.Review;
import com.codewithdondamzy.onlinestore.Repository.CustomerRepository;
import com.codewithdondamzy.onlinestore.Repository.ProductRepository;
import com.codewithdondamzy.onlinestore.Repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;


    @Override
    public ReviewResponse updateReview(ReviewRequest reviewRequest, Long reviewId) {
        ReviewResponse reviewResponse = new ReviewResponse();
        try {
            Optional<Review> review = reviewRepository.findById(reviewId);
            Optional<Products> products = productRepository.findProductsById(review.get().getProduct().getId());
            if(review.isEmpty()) {
                reviewResponse.setStatusCode(401);
                reviewResponse.setMessage("Review Not Found");
                return reviewResponse;
            }
            if(products.isEmpty()) {
                reviewResponse.setStatusCode(401);
                reviewResponse.setMessage("Product Not Found");
                return reviewResponse;
            }
            Products productToReview = products.get();
            Review reviewUpdate = Review.builder()
                    .rating(reviewRequest.getRating())
                    .comment(reviewRequest.getComment())
                    .createdAt(LocalDateTime.now())
                    .product(productToReview)
                    .build();
            reviewRepository.save(reviewUpdate);
            reviewResponse.setStatusCode(200);
            reviewResponse.setMessage("Review Updated");
            return reviewResponse;
        } catch (Exception e) {
            reviewResponse.setStatusCode(500);
            reviewResponse.setMessage("There was an error while updating the review");
            return reviewResponse;
        }
    }

    @Override
    public ReviewResponse getReview(Long reviewId) {
        ReviewResponse reviewResponse = new ReviewResponse();
        try {
            Optional<Review> review = reviewRepository.findById(reviewId);
            if(review.isEmpty()) {
                reviewResponse.setStatusCode(404);
                reviewResponse.setMessage("Review Not Found");
                return reviewResponse;
            }
            Review reviewToGet = review.get();
            reviewResponse.setStatusCode(200);
            reviewResponse.setMessage("Review gotten successfully!!");
            reviewResponse.setData(reviewToGet);
            return reviewResponse;
        } catch (Exception e) {
            reviewResponse.setStatusCode(500);
            reviewResponse.setMessage("There was an error while retrieving the review");
            return reviewResponse;
        }
    }

    @Override
    public ReviewResponse deleteReview(Long reviewId) {
        ReviewResponse reviewResponse = new ReviewResponse();
        try {
            Optional<Review> review = reviewRepository.findById(reviewId);
            if(review.isEmpty()) {
                reviewResponse.setStatusCode(404);
                reviewResponse.setMessage("Review Not Found");
                return reviewResponse;
            }
            Review reviewToDelete = review.get();
            reviewRepository.delete(reviewToDelete);
            reviewResponse.setStatusCode(200);
            reviewResponse.setMessage("Review deleted successfully!!");
            reviewResponse.setData(reviewToDelete);
            return reviewResponse;
        } catch (Exception e) {
            reviewResponse.setStatusCode(500);
            reviewResponse.setMessage("There was an error while deleting the review");
            return reviewResponse;
        }
    }

    @Override
    public ReviewResponse addReviewToProduct(Authentication authentication, ReviewRequest reviewRequest, String productName) {
        ReviewResponse reviewResponse = new ReviewResponse();
        String userName = authentication.getName();

        try {
            Customer customer = customerRepository.findByUserName(userName);
            if(!customer.getName().equals(userName)) {
                reviewResponse.setStatusCode(404);
                reviewResponse.setMessage("Customer Not Found");
                return reviewResponse;
            }
            Optional<Products> products = productRepository.findByName(productName);
            if(products.isEmpty()) {
                reviewResponse.setStatusCode(401);
                reviewResponse.setMessage("Product Not Found, cannot add review");
                return reviewResponse;
            }
            Products productsToAdd = products.get();

            Review review = Review.builder()
                    .customer(customer)
                    .product(productsToAdd)
                    .rating(reviewRequest.getRating())
                    .comment(reviewRequest.getComment())
                    .UUID(UUID.randomUUID().toString())
                    .createdAt(LocalDateTime.now())
                    .build();

            productsToAdd.getReviews().add(review);
            productRepository.save(productsToAdd);
            reviewRepository.save(review);
            reviewResponse.setStatusCode(200);
            reviewResponse.setMessage("Review added successfully!!");
            reviewResponse.setData(review);
            return reviewResponse;
        } catch (Exception e) {
            reviewResponse.setStatusCode(500);
            reviewResponse.setMessage("Unable to add review at the moment, pls try again!!");
            return reviewResponse;
        }


    }
}
