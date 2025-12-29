package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.CartItemRequest;
import com.codewithdondamzy.onlinestore.Service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/OnlineStore")
public class CartItemController {
    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PutMapping("addItemInCart/{cartId}/{productId}")
    public ResponseEntity<?> addItemInCart(@PathVariable Long cartId, @PathVariable Long productId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartItemService.addItemToCart(cartId, productId, quantity));
    }

    @DeleteMapping("removeItemFromCart/{cartId}/{productId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        return ResponseEntity.ok(cartItemService.removeItemFromCart(cartId, productId));
    }

    @PutMapping("/updateCartItemQunatity/{cartId}/{productId}")
    public ResponseEntity<?> updateCartItemQuantity(@PathVariable Long cartId, @PathVariable Long productId,@RequestParam Integer quantity) {
        return ResponseEntity.ok(cartItemService.updateCartItemQuantity(cartId, productId, quantity));
    }

}
