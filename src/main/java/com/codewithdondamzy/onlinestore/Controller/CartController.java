package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/OnlineStore")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/getCartById/{id}")
    public ResponseEntity<?> getCartById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.getCartsById(id));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @DeleteMapping("/clearCartById/{id}")
    public ResponseEntity<?> clearCartById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.clearCartById(id));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PutMapping("/addProductToCart/{cartId}/{productId}")
    public ResponseEntity<?> addProductToCart(@PathVariable Long cartId,
                                              @PathVariable Long productId,
                                              @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.addProductToCart(cartId, productId,quantity));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/getCartTotalPrice/{id}")
    public ResponseEntity<?> getCartTotalPrice(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.getCartTotalPrice(id));
    }
}
