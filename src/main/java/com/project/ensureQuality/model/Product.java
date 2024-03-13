package com.project.ensureQuality.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Blob;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "productName")
    private String productName;

    @Lob
    private Blob productImage;

    @Lob
    private Blob qrCode;

    @Column(name = "price")
    private float price;

    @Column(name = "total")
    private int total;
}
