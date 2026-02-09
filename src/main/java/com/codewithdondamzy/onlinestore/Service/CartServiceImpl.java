package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Response.CartResponse;
import com.codewithdondamzy.onlinestore.Models.Cart;
import com.codewithdondamzy.onlinestore.Models.CartItem;
import com.codewithdondamzy.onlinestore.Models.Products;
import com.codewithdondamzy.onlinestore.Repository.CartItemRepository;
import com.codewithdondamzy.onlinestore.Repository.CartRepository;
import com.codewithdondamzy.onlinestore.Repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public CartResponse getCartsById(Long id) {
        CartResponse cartResponse = new CartResponse();
        try {
            Cart cart = cartRepository.findById(id).orElseThrow(()
                    -> new RuntimeException("Cart not found"));
            BigDecimal totalPrice = cart.getTotalPrice();
            cart.setTotalPrice(totalPrice);
            cartRepository.save(cart);
            cartResponse.setStatusCode(200);
            cartResponse.setMessage("Success");
            cartResponse.setData(cart);
            return cartResponse;
        } catch (RuntimeException e) {
            cartResponse.setStatusCode(500);
            cartResponse.setMessage(e.getMessage());
            return cartResponse;
        }

    }

    @Transactional
    @Override
    public CartResponse clearCartById(Long id) {
        CartResponse cartResponse = new CartResponse();
        try {
            Cart cart = cartRepository.findById(id).orElseThrow(()
                    -> new RuntimeException("Cart not found"));
            cart.getCartItem().clear();
            cartRepository.deleteCartById(id);
            cartResponse.setStatusCode(200);
            cartResponse.setMessage("Cart has been cleared successfully!!");
            return cartResponse;
        } catch (RuntimeException e) {
            cartResponse.setStatusCode(500);
            cartResponse.setMessage("Cart could not be cleared successfully!!");
            return cartResponse;
        }
    }

    @Override
    public CartResponse addProductToCart(Long cartId, Long productId, int quantity) {
        CartResponse cartResponse = new CartResponse();
        Optional<Cart> cart = cartRepository.findById(cartId);
        Optional<Products> products = productRepository.findProductsById(productId);
        if (cart.isEmpty()) {
            cartResponse.setStatusCode(400);
            cartResponse.setMessage("Cart not found");
            return cartResponse;
        }
        if (products.isEmpty()) {
            cartResponse.setStatusCode(400);
            cartResponse.setMessage("Product not found , cannot be added to cart");
            return cartResponse;
        }
        Cart cart1 = cart.get();
        Products product = products.get();
        if (cart1.getCartItem() == null) {
            CartItem cartItem = CartItem.builder()
                    .cart(cart1)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .totalPrice(product.getPrice())
                    .build();
            cart1.addItem(cartItem);
            cartRepository.save(cart1);
            cartResponse.setStatusCode(200);
            cartResponse.setMessage("Product added to cart successfully!!");
            return cartResponse;
        }
        for (CartItem cartItem : cart1.getCartItem()) {
            if (cartItem.getProduct().getId().equals(productId)) {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItemRepository.save(cartItem);
                cartResponse.setStatusCode(200);
                cartResponse.setMessage("Product added to cart successfully and quantity updated!!");
                return cartResponse;
            }
            CartItem cartItem1 = CartItem.builder()
                    .cart(cart1)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .totalPrice(product.getPrice())
                    .build();
            cart1.addItem(cartItem1);
            cartRepository.save(cart1);
            cartResponse.setStatusCode(200);
            cartResponse.setMessage("Product added to cart successfully!!");
            return cartResponse;
        }
        return cartResponse;
    }


    @Override
    public CartResponse getCartTotalPrice(Long id) {
        CartResponse cartResponse = new CartResponse();
        try {
            Cart cart = cartRepository.findById(id).orElseThrow(()
                    -> new RuntimeException("Cart not found"));
            BigDecimal totalPrice = BigDecimal.ZERO;
            if(cart.getCartItem() != null) {
                for (CartItem cartItem : cart.getCartItem()) {
                    if (cartItem.getTotalPrice() != null) {
                        totalPrice = cartItem.getTotalPrice();
                        break;
                    }
                }
            }
            cartResponse.setStatusCode(200);
            cartResponse.setMessage("Total price of cart item gotten successfully!!");
            cartResponse.setData(totalPrice);
            return cartResponse;
        } catch (RuntimeException e) {
            cartResponse.setStatusCode(500);
            cartResponse.setMessage("Total price of cart item could not be gotten successfully!!");
            return cartResponse;
        }
    }

}
