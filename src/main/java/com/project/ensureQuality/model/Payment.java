package com.project.ensureQuality.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int discount = 0; // giảm giá
    private int total; // tổng tiền cần thanh toán
    private int customerPaid; // tiền khách đã trả
    private int refunds; // tiền trả lại
}
