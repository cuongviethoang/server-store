package com.project.ensureQuality.controller;

import com.project.ensureQuality.model.Order;
import com.project.ensureQuality.security.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    OrderService orderService;

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping("/order-all")
    @PreAuthorize("hasRole('STAFF')")
    public List<Order> getAllOrder(){
        return orderService.getAllOrder();
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping("/order/get/{order_id}")
    public Order getOrderById(@PathVariable("order_id") int order_id){
        return orderService.getOrderById(order_id);
    }
}
