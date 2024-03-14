package com.project.ensureQuality.security.services.servicesImpl;

import com.project.ensureQuality.exception.ResourceNotFoundException;
import com.project.ensureQuality.model.Product;
import com.project.ensureQuality.payload.response.MessageResponse;
import com.project.ensureQuality.repository.ProductRepository;
import com.project.ensureQuality.security.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServicesImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;
    @Override
    public Product addNewProduct(String productName, MultipartFile productImage, MultipartFile qrCode, float price, int total) throws SQLException, IOException {
        Product product = new Product();
        product.setProductName(productName);
        product.setPrice(price);
        product.setTotal(total);
        if(!productImage.isEmpty()) {
            byte[] photoBytes = productImage.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            product.setProductImage(photoBlob);
        }
        if(!qrCode.isEmpty()) {
            byte[] photoBytes = qrCode.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            product.setQrCode(photoBlob);
        }

        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts(int page, int limit) {
        int offset = (page-1) * limit;
        return productRepository.getProductsWithPagination(offset, limit);
    }

    @Override
    public byte[] getProductImageByProductId(Integer productId) throws SQLException {
        Optional<Product> theProduct = productRepository.findById(productId);
        if(theProduct.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm");
        }
        Blob photoBlob = theProduct.get().getProductImage();
        if(photoBlob != null) {
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }

    @Override
    public byte[] getQrCodeByProductId(Integer productId) throws SQLException {
        Optional<Product> theProduct = productRepository.findById(productId);
        if(theProduct.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm");
        }

        Blob photoBlob = theProduct.get().getQrCode();
        if(photoBlob != null) {
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }

    @Override
    public Optional<Product> getProductById(Integer productId) {
        return Optional.of(productRepository.findById(productId)).get();
    }

    @Override
    public int getAllNumberProducts() {
        try {
            List<Product> products = productRepository.findAll();
            return products.size();
        } catch (Exception e) {
            return 0;
        }
    }
}
