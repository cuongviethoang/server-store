package com.project.ensureQuality.controller;

import com.project.ensureQuality.exception.PhotoRetrievaException;
import com.project.ensureQuality.model.ItemOrder;
import com.project.ensureQuality.model.Order;
import com.project.ensureQuality.model.Product;
import com.project.ensureQuality.payload.response.ItemOrderResponse;
import com.project.ensureQuality.payload.response.OrderResponse;
import com.project.ensureQuality.payload.response.ProductResponse;
import com.project.ensureQuality.security.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    OrderService orderService;

    @GetMapping("/order-all")
    public List<OrderResponse> getAllOrder(){
        List<Order> orders=orderService.getAllOrder();
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order:orders){
            OrderResponse orderResponse=new OrderResponse(
                    order.getId(),
                    order.getCode(),
                    order.getCreateTime(),
                    order.getCustomer(),
                    getItemOrderResponse(order.getItemOrders()),
                    order.getPayment(),
                    order.getUser()
            );
            orderResponses.add(orderResponse);
        }
        return orderResponses;
    }

    @GetMapping("/order/get/{order_id}")
    public Order getOrderById(@PathVariable("order_id") int order_id){
        return orderService.getOrderById(order_id);
    }
    @GetMapping("/order/test")
    public String getTest(){
        return "siiuuu";
    }

    private List<ItemOrderResponse> getItemOrderResponse(List<ItemOrder> itemOrders) {
        List<ItemOrderResponse> itemOrderResponses=new ArrayList<>();
        for (ItemOrder itemOrder:itemOrders){
            ItemOrderResponse itemOrderResponse= new ItemOrderResponse(
                    itemOrder.getId(),
                    getProductResponse(itemOrder.getProduct()),
                    itemOrder.getQuantity(),
                    itemOrder.getPrice()
            );
            itemOrderResponses.add(itemOrderResponse);
        }
        return  itemOrderResponses;
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
