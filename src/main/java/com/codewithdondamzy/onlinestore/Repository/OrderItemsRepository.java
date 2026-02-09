package com.codewithdondamzy.onlinestore.Repository;

import com.codewithdondamzy.onlinestore.Models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemsRepository extends JpaRepository <OrderItem, Long> {

}
