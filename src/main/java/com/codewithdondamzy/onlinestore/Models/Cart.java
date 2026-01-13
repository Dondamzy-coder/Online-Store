package com.codewithdondamzy.onlinestore.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalPrice = BigDecimal.ZERO;


    @OneToOne
    @JoinColumn(name = "customer_id",nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> cartItem;


    public void addItem(CartItem cartItem) {
        this.cartItem.add(cartItem);
        cartItem.setCart(this);
        updateTotalPrice();
    }

    public void removeItem(CartItem cartItem) {
        this.cartItem.remove(cartItem);
        cartItem.setCart(null);
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        this.totalPrice = cartItem.stream()
                .map(CartItem -> {
                    BigDecimal price = CartItem.getTotalPrice();
                    if (price == null) {
                        return BigDecimal.ZERO;
                    }
                    return price.multiply(new BigDecimal(CartItem.getQuantity()));
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
