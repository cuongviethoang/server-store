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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public PaginationItemOrderResponse getAllItemOrderOfOrder(int orderId, int limit) {
        try {
            Order order = orderRepository.findById(orderId).get();
            List<ItemOrder> itemOrders = order.getItemOrders();
            PaginationItemOrderResponse paginationItemOrderResponse = new PaginationItemOrderResponse();
            paginationItemOrderResponse.setTotal_item(itemOrders.size());
            paginationItemOrderResponse.setPer_page(10);

            int total_page = (int) Math.ceil((double) itemOrders.size() / 10);
            paginationItemOrderResponse.setTotal_page(total_page);

            if(limit * 10 > itemOrders.size()) {
                paginationItemOrderResponse.setData(itemOrders.subList((limit-1)*10, itemOrders.size()));
            }
            else {
                paginationItemOrderResponse.setData(itemOrders.subList((limit-1)*10, limit*10));
            }
            return paginationItemOrderResponse;
        } catch (Exception e) {
            return null;
        }

    }


}
