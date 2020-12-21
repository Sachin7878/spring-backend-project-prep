package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entities.OrderModel;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {
	OrderModel findByCustomerId(Long customerId);
}
