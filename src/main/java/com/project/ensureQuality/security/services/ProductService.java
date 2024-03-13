package com.project.ensureQuality.security.services;

import com.project.ensureQuality.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product addNewProduct(String productName, MultipartFile productImage, MultipartFile qrCode, float price, int total)  throws SQLException, IOException;

    List<Product> getAllProducts(int page, int limit);

    byte[] getProductImageByProductId(Integer productId) throws SQLException;

    byte[] getQrCodeByProductId(Integer productId) throws SQLException;

    Optional<Product> getProductById(Integer productId);
}
