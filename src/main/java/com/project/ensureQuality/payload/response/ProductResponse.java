package com.project.ensureQuality.payload.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.tomcat.util.codec.binary.Base64;

@Data
@NoArgsConstructor
public class ProductResponse {

    private Integer id;

    private String productName;

    private String productImage;
    private String qrCode;
    private float price;
    private int total;

    public ProductResponse(Integer id, String productName, float price, int total) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.total = total;
    }

    public ProductResponse(Integer id, String productName, byte[] productImage, float price, int total) {
        this.id = id;
        this.productName = productName;
        this.productImage = productImage != null ? Base64.encodeBase64String(productImage) : null;
        this.price = price;
        this.total = total;
    }

    public ProductResponse(Integer id, String productName, byte[] productImage, byte[] qrCode, float price, int total) {
        this.id = id;
        this.productName = productName;
        this.productImage = productImage != null ? Base64.encodeBase64String(productImage) : null;
        this.qrCode = qrCode != null ? Base64.encodeBase64String(qrCode) : null;
        this.price = price;
        this.total = total;
    }
}
