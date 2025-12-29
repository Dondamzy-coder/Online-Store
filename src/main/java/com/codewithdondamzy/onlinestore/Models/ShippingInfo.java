package com.codewithdondamzy.onlinestore.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int shippingId;
    private String shippingType;
    private int shippingCost;
    private int shippingRegionId;
    @OneToOne
    @JoinColumn(name = "order_id",unique = true)
    private Order order;

}
