package com.youtube.ecommerce.repository;

import com.youtube.ecommerce.dao.OrderDetailRepositoryCustom;
import com.youtube.ecommerce.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

//    public Optional<OrderDetail> findById(Integer id);
}


