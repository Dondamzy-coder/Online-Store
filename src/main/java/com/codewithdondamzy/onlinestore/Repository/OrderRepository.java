package com.codewithdondamzy.onlinestore.Repository;

import com.codewithdondamzy.onlinestore.Models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findOrderById(Long id);

    Long id(Long id);

    Optional<Order> findOrderByDateCreated(LocalDate dateCreated);

    List<Order> findOrderByCustomer_Id(Long customerId);
}
