package com.ecommerce.payment.repository;

import com.ecommerce.payment.entity.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionDetail, Integer> {

    List<TransactionDetail> findByUserEmail(String userEmail);

    Optional<TransactionDetail> findByRazorpayOrderId(String razorpayOrderId);

    List<TransactionDetail> findByOrderId(Integer orderId);

    List<TransactionDetail> findByStatus(String status);
}
