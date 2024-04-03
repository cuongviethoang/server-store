package com.project.ensureQuality.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    @Column(name = "created")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private Date createTime = new Date();

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL
    )
    private List<ItemOrder> itemOrders;

    @OneToOne(
            cascade = CascadeType.ALL
    )
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Nhân viên tạo đơn
}
