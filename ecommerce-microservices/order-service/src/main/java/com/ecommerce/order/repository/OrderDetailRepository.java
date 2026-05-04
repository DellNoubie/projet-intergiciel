package com.ecommerce.order.repository;

import com.ecommerce.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    List<OrderDetail> findByUserEmail(String userEmail);

    List<OrderDetail> findByOrderStatus(String status);

    List<OrderDetail> findByUserEmailOrderByOrderDateDesc(String userEmail);
}
