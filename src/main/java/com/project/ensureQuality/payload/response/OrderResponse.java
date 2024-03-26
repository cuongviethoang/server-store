package com.project.ensureQuality.payload.response;

import com.project.ensureQuality.model.Customer;
import com.project.ensureQuality.model.ItemOrder;
import com.project.ensureQuality.model.Payment;
import com.project.ensureQuality.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderResponse {
    private Integer id;

    private String code;

    private Date createTime = new Date();

    private Customer customer;

    private List<ItemOrderResponse> itemOrders;

    private Payment payment;

    private User user; // Nhân viên tạo đơn
}
