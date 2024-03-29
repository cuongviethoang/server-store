package com.project.ensureQuality.security.services;

import com.project.ensureQuality.model.Order;
import com.project.ensureQuality.payload.response.MessageResponse;

import java.util.List;

public interface OrderService {

    MessageResponse addNewOrder(Order order);
    List<Order> getAllOrder();

    Order getOrderById(int order_id);
}
