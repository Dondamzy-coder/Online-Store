package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/OnlineStore")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/getCartById/{id}")
    public ResponseEntity<?> getCartById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.getCartsById(id));
    }

    @DeleteMapping("/clearCartById/{id}")
    public ResponseEntity<?> clearCartById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.clearCartById(id));
    }

    @PutMapping("/addProductToCart/{cartId}/{productId}")
    public ResponseEntity<?> addProductToCart(@PathVariable Long cartId,
                                              @PathVariable Long productId,
                                              @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.addProductToCart(cartId, productId,quantity));
    }

    @GetMapping("/getCartTotalPrice/{id}")
    public ResponseEntity<?> getCartTotalPrice(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.getCartTotalPrice(id));
    }


}
