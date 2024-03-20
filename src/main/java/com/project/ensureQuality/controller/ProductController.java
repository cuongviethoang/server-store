package com.project.ensureQuality.controller;


import com.project.ensureQuality.exception.PhotoRetrievaException;
import com.project.ensureQuality.model.Product;
import com.project.ensureQuality.payload.response.MessageResponse;
import com.project.ensureQuality.payload.response.ProductResponse;
import com.project.ensureQuality.security.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@CrossOrigin(allowCredentials = "true")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    @Autowired
    ProductService productService;

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping("/product/create")
    public ResponseEntity<?> addNewProduct(Authentication authentication,  @RequestParam("productName") String productName,
                                           @RequestParam("productImage") MultipartFile productImage,
                                           @RequestParam("qrCode") MultipartFile qrCode,
                                           @RequestParam("price") float price,
                                           @RequestParam("total") int total
    )  throws SQLException, IOException {
        Product savedProduct = productService.addNewProduct(productName, productImage, qrCode, price, total);
        ProductResponse productResponse = new ProductResponse(savedProduct.getId(), savedProduct.getProductName(),
                savedProduct.getPrice(), savedProduct.getTotal());


        return ResponseEntity.status(200).body(productResponse);
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping("/product/read-all-number")
    public ResponseEntity<?> getAllNumberProduct() {
        try {
            int leng = productService.getAllNumberProducts();
            return ResponseEntity.status(200).body(leng);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error server", -1));
        }
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping("/product/read-all")
    public ResponseEntity<?> getAllProducts(@RequestParam int page, @RequestParam int limit) throws SQLException {
        List<Product> products = productService.getAllProducts(page, limit);
        List<ProductResponse> productResponses = new ArrayList<>();
        for(Product p : products){
            byte[] photoBytesProductImg = productService.getProductImageByProductId(p.getId());
            byte[] photoBytesQrCode = productService.getQrCodeByProductId(p.getId());
            if(photoBytesProductImg != null && photoBytesProductImg.length > 0 && photoBytesQrCode != null && photoBytesQrCode.length > 0) {
                String base64Photo1 = Base64.encodeBase64String(photoBytesProductImg);
                String base64Photo2 = Base64.encodeBase64String(photoBytesQrCode);
                ProductResponse productResponse = getProductResponse(p);
                productResponse.setProductImage(base64Photo1);
                productResponse.setQrCode(base64Photo2);
                productResponses.add(productResponse);
            }
        }
        return ResponseEntity.status(200).body(productResponses);
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping("/product/{proId}")
    public ResponseEntity<?> getDetailProduct(@PathVariable(value = "proId") int proId) {
        try {
            Optional<Product> product = productService.getProductById(proId);
            ProductResponse productResponse = new ProductResponse();
            if(!product.isEmpty()) {
                byte[] photoBytesProductImg = productService.getProductImageByProductId(product.get().getId());
                byte[] photoBytesQrCode = productService.getQrCodeByProductId(product.get().getId());
                if(photoBytesProductImg != null && photoBytesProductImg.length > 0 && photoBytesQrCode != null && photoBytesQrCode.length > 0) {
                    String base64Photo1 = Base64.encodeBase64String(photoBytesProductImg);
                    String base64Photo2 = Base64.encodeBase64String(photoBytesQrCode);
                    productResponse = getProductResponse(product.get());
                    productResponse.setProductImage(base64Photo1);
                    productResponse.setQrCode(base64Photo2);
                    return ResponseEntity.status(200).body(productResponse);
                }
                else {
                    return ResponseEntity.status(400).body(new MessageResponse("Không tải được ảnh sản phẩm!", 1));
                }
            }
            else {
                return ResponseEntity.status(400).body(new MessageResponse("Không tải được thông tin sản phẩm!", 1));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error server", -1));
        }
    }

    private ProductResponse getProductResponse(Product product) {
        byte[] photoBytes1 = null;
        byte[] photoBytes2 = null;
        Blob productImage = product.getProductImage();
        Blob qrCode = product.getQrCode();
        if(productImage != null && qrCode != null) {
            try {
                photoBytes1 = productImage.getBytes(1, (int) productImage.length());
                photoBytes2 = qrCode.getBytes(1, (int) qrCode.length());
            } catch (SQLException e) {
                throw  new PhotoRetrievaException("Lỗi truy xuất ảnh");
            }
        }
        return new ProductResponse(product.getId(), product.getProductName(), photoBytes1, photoBytes2, product.getPrice(), product.getTotal());
    }
}
