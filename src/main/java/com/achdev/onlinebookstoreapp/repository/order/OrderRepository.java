package com.achdev.onlinebookstoreapp.repository.order;

import com.achdev.onlinebookstoreapp.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserEmail(String userEmail, Pageable pageable);
}
