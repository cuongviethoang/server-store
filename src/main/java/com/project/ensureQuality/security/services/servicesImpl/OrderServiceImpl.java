package com.project.ensureQuality.security.services.servicesImpl;

import com.project.ensureQuality.model.ItemOrder;
import com.project.ensureQuality.model.Order;
import com.project.ensureQuality.payload.response.MessageResponse;
import com.project.ensureQuality.payload.response.PaginationItemOrderResponse;
import com.project.ensureQuality.payload.response.PaginationOrderResponse;
import com.project.ensureQuality.repository.ItemOrderRepository;
import com.project.ensureQuality.repository.OrderRepository;
import com.project.ensureQuality.security.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemOrderRepository itemOrderRepository;

    @Override
    public MessageResponse addNewOrder(Order order) {
        Order newOrder =  orderRepository.save(order);
        List<ItemOrder> itemOrders = order.getItemOrders();

        for (ItemOrder itemOrder : itemOrders) {
            itemOrder.setOrder(newOrder);
        }

        itemOrderRepository.saveAll(itemOrders);
        return new MessageResponse("Tạo Order thành công", 0);
    }

    @Override
    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(int order_id) {
        return orderRepository.findById(order_id).orElse(new Order());
    }

    @Override
    public List<Order> getAllOrdersSearchWithPagination(
            String key, Date startTime, Date endTime, Pageable pageable) {
        return orderRepository.getAllOrdersSearchWithPagination(
                key, startTime, endTime, pageable).getContent();
    }

    @Override
    public int getAllOrdersSearchWithPaginationNum(String key, Date startTime, Date endTime) {
        return orderRepository.getAllOrdersSearchWithPaginationNum(key, startTime, endTime)
                .orElse(new ArrayList<>()).size();
    }


}
