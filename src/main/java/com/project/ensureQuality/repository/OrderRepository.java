package com.project.ensureQuality.repository;

import com.project.ensureQuality.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
    @Query("SELECT DISTINCT o FROM Order o JOIN o.itemOrders io WHERE " +
            "(CAST(o.id AS string) LIKE %:key% " +
            "OR o.customer.username LIKE %:key% " +
            "OR io.product.productName LIKE %:key%) " +
            "AND (o.createTime BETWEEN:startTime AND :endTime) " +
            "order by o.id desc")
    Page<Order> getAllOrdersSearchWithPagination(
            String key, Date startTime, Date endTime, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.itemOrders io WHERE " +
            "(CAST(o.id AS string) LIKE %:key% " +
            "OR o.customer.username LIKE %:key% " +
            "OR io.product.productName LIKE %:key%) " +
            "AND (o.createTime BETWEEN:startTime AND :endTime) " +
            "order by o.id desc")
    Optional<List<Order>> getAllOrdersSearchWithPaginationNum(
            String key, Date startTime, Date endTime);
}
