package com.youtube.ecommerce.entity;

import com.youtube.ecommerce.dao.OrderDetailRepositoryCustom;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class OrderDetailRepositoryImpl implements OrderDetailRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getOrderProductList() {
        String jpql = "SELECT p.productName, od.orderAmount, od.orderContactNumber, od.orderFullName, od.orderFullOrder, od.orderStatus, od.transactionId, od.time"
                + " FROM OrderDetail od"
                + " LEFT JOIN Product p ON od.product.productID = p.productID";
        Query query = entityManager.createQuery(jpql);
        return query.getResultList();
    }

}
