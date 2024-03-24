package com.project.ensureQuality.security.services.servicesImpl;

import com.project.ensureQuality.model.Order;
import com.project.ensureQuality.repository.OrderRepository;
import com.project.ensureQuality.security.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Override
    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(int order_id) {
        return orderRepository.findById(order_id).orElse(new Order());
    }


}
