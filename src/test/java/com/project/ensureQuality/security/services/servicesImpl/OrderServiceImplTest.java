package com.project.ensureQuality.security.services.servicesImpl;

import com.project.ensureQuality.model.ItemOrder;
import com.project.ensureQuality.model.Order;
import com.project.ensureQuality.payload.response.MessageResponse;
import com.project.ensureQuality.repository.ItemOrderRepository;
import com.project.ensureQuality.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemOrderRepository itemOrderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addNewOrder_Success() {
        Order order = new Order();
        List<ItemOrder> itemOrders = new ArrayList<>();
        itemOrders.add(new ItemOrder());
        order.setItemOrders(itemOrders);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        MessageResponse response = orderService.addNewOrder(order);

        assertEquals("Tạo Order thành công", response.getEM());
        assertEquals(0, response.getEC());
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(itemOrderRepository, times(1)).saveAll(anyList());
    }

    @Test
    void getAllOrder() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1));
        orders.add(new Order(2));
        //thiết lập hành vi trả về cho các phương thức của repository được gọi trong hàm dưới thử nghiệm.
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrder();
        assertEquals(orders, result);
        // kiểm tra xem các phương thức của repository đã được gọi đúng số lần và với các tham số đúng hay không.
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById_OrderExists() {
        Order order = new Order();
        order.setId(1);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1);
        assertEquals(order, result);

        verify(orderRepository, times(1)).findById(1);
    }


    @Test
    void getOrderById_OrderNotExists() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        Order result = orderService.getOrderById(1);
        assertEquals(new Order(), result);

        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    void getAllOrdersSearchWithPagination() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1));
        orders.add(new Order(2));
        Page<Order> page = new PageImpl<>(orders);
        when(orderRepository.getAllOrdersSearchWithPagination(
                anyString(), any(Date.class), any(Date.class), any(Pageable.class))).thenReturn(page);

        List<Order> result = orderService.getAllOrdersSearchWithPagination("key", new Date(), new Date(), Pageable.unpaged());
        assertEquals(orders, result);
        verify(orderRepository, times(1)).getAllOrdersSearchWithPagination(anyString(), any(Date.class), any(Date.class), any(Pageable.class));
    }

    @Test
    void getAllOrdersSearchWithPaginationNum() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1));
        orders.add(new Order(2));
        when(orderRepository.getAllOrdersSearchWithPaginationNum(anyString(), any(Date.class), any(Date.class)))
                .thenReturn(Optional.of(orders));

        int result = orderService.getAllOrdersSearchWithPaginationNum("key", new Date(), new Date());

        assertEquals(orders.size(), result);
        verify(orderRepository, times(1))
                .getAllOrdersSearchWithPaginationNum(anyString(), any(Date.class), any(Date.class));
    }
}
