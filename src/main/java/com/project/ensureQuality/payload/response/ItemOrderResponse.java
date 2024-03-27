package com.project.ensureQuality.payload.response;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.ensureQuality.model.Order;
import com.project.ensureQuality.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemOrderResponse {
    private Integer id;
    private ProductResponse product;
    private int quantity;
    private int price;
}