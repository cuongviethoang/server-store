package com.project.ensureQuality.security.services;

import com.project.ensureQuality.model.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrder();

    Order getOrderById(int order_id);
}
