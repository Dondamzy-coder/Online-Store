package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Response.CartItemResponse;
import com.codewithdondamzy.onlinestore.Models.Cart;
import com.codewithdondamzy.onlinestore.Models.CartItem;
import com.codewithdondamzy.onlinestore.Models.Products;
import com.codewithdondamzy.onlinestore.Repository.CartItemRepository;
import com.codewithdondamzy.onlinestore.Repository.CartRepository;
import com.codewithdondamzy.onlinestore.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class CartItemServiceImpl implements CartItemService{
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public CartItemServiceImpl(CartItemRepository cartItemRepository, ProductRepository productRepository, CartRepository cartRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }
    @Override
    public CartItemResponse addItemToCart(Long cartId, Long productId, int quantity) {
        CartItemResponse cartItemResponse = new CartItemResponse();
        Optional<Cart> cart = cartRepository.findById(cartId);
        Optional<Products>  product = productRepository.findById(productId);
        if(cart.isEmpty()){
            cartItemResponse.setStatusCode(401);
            cartItemResponse.setMessage("cart not found");
            return cartItemResponse;
        }
        if(product.isEmpty()){
            cartItemResponse.setStatusCode(401);
            cartItemResponse.setMessage("product not found");
            return cartItemResponse;
        }
        Cart newCart = cart.get();
        Products newProduct = product.get();
        CartItem cartItem = null;
        if (newCart.getCartItem() != null) {
            for (CartItem item : newCart.getCartItem()) {
                if (item.getProduct() != null
                        && item.getProduct().getId().equals(productId)) {

                    cartItem = item;
                    break; // stop once we find the first match
                }
                assert false;
                if (cartItem.getId() == null) {
                    cartItem.setCart(newCart);
                    cartItem.setProduct(newProduct);
                    cartItem.setQuantity(quantity);
                    cartItem.setTotalPrice(newProduct.getPrice());
                }
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
            }

        }
        assert cartItem != null;
        cartItem.calculateTotalPrice();
        cartItem.setQuantity(quantity);
        cartItem.setCart(newCart);
        cartItemResponse.setStatusCode(200);
        cartItemResponse.setMessage("Item added to cart");
        cartItemRepository.save(cartItem);
        cartRepository.save(newCart);
        return cartItemResponse;
    }


    @Override
    public CartItemResponse removeItemFromCart(Long cartId, Long productId) {
        CartItemResponse cartItemResponse = new CartItemResponse();
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        Optional<Products> productOptional = productRepository.findById(productId);

        if (cartOptional.isEmpty()) {
            cartItemResponse.setStatusCode(401);
            cartItemResponse.setMessage("cart not found");
            return cartItemResponse;
        }

        if (productOptional.isEmpty()) {
            cartItemResponse.setStatusCode(401);
            cartItemResponse.setMessage("product not found");
            return cartItemResponse;
        }

        Cart cart = cartOptional.get();
        CartItem itemToRemove = null;

        if (cart.getCartItem() != null) {
            for (CartItem item : cart.getCartItem()) {
                if (item.getProduct() != null
                        && item.getProduct().getId().equals(productId)) {

                    itemToRemove = item;
                    break; // same as findFirst()
                }
            }
        }

        if (itemToRemove == null) {
            throw new RuntimeException("product not found");
        }

        // remove item from cart
        cart.removeItem(itemToRemove);
        cartRepository.save(cart);
        cartItemResponse.setStatusCode(200);
        cartItemResponse.setMessage("Item removed successfully");
        return cartItemResponse;
    }

    @Override
    public CartItemResponse updateCartItemQuantity(Long cartId, Long productId, int quantity) {
        CartItemResponse cartItemResponse = new CartItemResponse();
        if(quantity <= 0) {
            cartItemResponse.setStatusCode(400);
            cartItemResponse.setMessage("quantity must be greater than 0");
            return cartItemResponse;
        }
        Optional<Cart> cart = cartRepository.findById(cartId);
        if(cart.isEmpty()){
            cartItemResponse.setStatusCode(404);
            cartItemResponse.setMessage("cart not found");
            return cartItemResponse;
        }
        Cart cart1 = cart.get();
        CartItem itemToUpdate = null;
        if(cart1.getCartItem() != null) {
            for(CartItem item : cart1.getCartItem()) {
                if (item.getProduct() != null && item.getProduct().getId().equals(productId)) {
                    itemToUpdate = item;
                    break;

                }
            }
        }
        if(itemToUpdate == null){
            cartItemResponse.setStatusCode(500);
            cartItemResponse.setMessage("Product not found cannot be updated!!");
            return cartItemResponse;
        }
        itemToUpdate.setQuantity(quantity);
        itemToUpdate.setUnitPrice(itemToUpdate.getProduct().getPrice());
        itemToUpdate.setTotalPrice(itemToUpdate.getProduct().getPrice());
        cartRepository.save(cart1);
        cartItemResponse.setStatusCode(200);
        cartItemResponse.setMessage("Item updated successfully");
        return cartItemResponse;
    }
}
