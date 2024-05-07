package com.project.ensureQuality.repository;

import com.project.ensureQuality.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void getAllOrdersSearchWithPagination() {
        String key = "";
        Date startTime;
        Date endTime;
        Pageable pageable = PageRequest.of(0, 10);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            startTime = dateFormat.parse("2024-01-01T00:00");
            endTime = dateFormat.parse("2025-01-01T00:00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Act
        Page<Order> result = orderRepository.getAllOrdersSearchWithPagination(
                key, startTime, endTime, pageable);
        List<Order> ordersResult=result.getContent();
        // Assert
        assertEquals(10, ordersResult.size());
    }

    @Test
    void getAllOrdersSearchWithPaginationNum() {
        String key = "";
        Date startTime;
        Date endTime;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            startTime = dateFormat.parse("2024-01-01T00:00");
            endTime = dateFormat.parse("2025-01-01T00:00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // Act
        Optional<List<Order>> result = orderRepository.getAllOrdersSearchWithPaginationNum(
                key, startTime, endTime);

        // Assert
        assertEquals(15, result.get().size());
    }
}

