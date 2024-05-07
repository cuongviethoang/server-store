package com.project.ensureQuality.controller;

import com.project.ensureQuality.exception.PhotoRetrievaException;
import com.project.ensureQuality.model.ItemOrder;
import com.project.ensureQuality.model.Order;
import com.project.ensureQuality.model.Product;
import com.project.ensureQuality.payload.response.*;
import com.project.ensureQuality.security.services.OrderService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addNewOrder_Success() {
        Order order = new Order();
        when(orderService.addNewOrder(any(Order.class))).thenReturn(new MessageResponse("Success", 0));

        ResponseEntity<?> response = orderController.addNewOrder(order);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).addNewOrder(any(Order.class));
    }

    @Test
    void addNewOrder_Failure() {
        Order order = new Order();
        when(orderService.addNewOrder(any(Order.class))).thenReturn(new MessageResponse("Failed", -1));

        ResponseEntity<?> response = orderController.addNewOrder(order);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(orderService, times(1)).addNewOrder(any(Order.class));
    }

    @Test
    void getAllOrder() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1));
        when(orderService.getAllOrder()).thenReturn(orders);

        List<OrderResponse> result = orderController.getAllOrder();

        assertEquals(orders.size(), result.size());
        verify(orderService, times(1)).getAllOrder();
    }

    @Test
    void getAllOrdersSearchWithPagination() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1));
        when(orderService.getAllOrdersSearchWithPagination(
                anyString(), any(Date.class), any(Date.class), any(Pageable.class))).thenReturn(orders);

        List<OrderResponse> result = orderController.getAllOrdersSearchWithPagination(
                1, 10, "key",
                "2024-01-01T00:00",
                "2025-01-01T00:00");
        assertEquals(orders.size(), result.size());
        verify(orderService, times(1))
                .getAllOrdersSearchWithPagination(anyString(), any(Date.class), any(Date.class), any(Pageable.class));
    }

    @Test
    void getAllOrdersSearchWithPaginationNum() {
        when(orderService.getAllOrdersSearchWithPaginationNum(
                anyString(), any(Date.class), any(Date.class))).thenReturn(5);

        int result = orderController.getAllOrdersSearchWithPaginationNum(
                "key",
                "2024-01-01T00:00",
                "2025-01-01T00:00");

        assertEquals(5, result);
        verify(orderService, times(1))
                .getAllOrdersSearchWithPaginationNum(anyString(), any(Date.class), any(Date.class));
    }

    @Test
    void getOrderById() {
        Order order = new Order(1);
        when(orderService.getOrderById(1)).thenReturn(order);

        Order result = orderController.getOrderById(1);

        assertEquals(order, result);
        verify(orderService, times(1)).getOrderById(1);
    }

    @Test
    public void getListItemOrderOfOrderReturnsDataSuccessfully() {
        // Setup
        int orderId = 1;
        Order order = new Order();
        List<ItemOrder> itemOrders = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            itemOrders.add(new ItemOrder());
        }
        order.setItemOrders(itemOrders);


        when(orderService.getOrderById(orderId)).thenReturn(order);

        // Execute
        ResponseEntity<?> responseEntity = orderController.getListItemOrderOfOrder(orderId, 1);

        // Verify
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(PaginationItemOrderResponse.class, responseEntity.getBody());

        PaginationItemOrderResponse response = (PaginationItemOrderResponse) responseEntity.getBody();
        assertEquals(10, response.getPer_page());
        assertEquals(2, response.getTotal_page());
        assertEquals(20, response.getTotal_item());
        assertEquals(10, response.getData().size());
    }

    @Test
    public void getListItemOrderOfOrderHandlesException() {
        int orderId = 1;
        when(orderService.getOrderById(orderId)).thenThrow(new RuntimeException("Server error!"));

        // Execute
        ResponseEntity<?> responseEntity = orderController.getListItemOrderOfOrder(orderId, 1);

        // Verify
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertInstanceOf(MessageResponse.class, responseEntity.getBody());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assertEquals("Lỗi server", messageResponse.getEM());
        assertEquals(-2, messageResponse.getEC());
    }

    @Test
    void getItemOrderResponse_ValidItemOrders_ReturnsItemOrderResponses() {
        // Arrange
        ItemOrder itemOrder1 = new ItemOrder();
        ItemOrder itemOrder2 = new ItemOrder();
        List<ItemOrder> itemOrders = new ArrayList<>();
        itemOrders.add(itemOrder1);
        itemOrders.add(itemOrder2);

        List<ItemOrderResponse> result = orderController.getItemOrderResponse(itemOrders);

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void getProductResponse_ValidProduct_ReturnsProductResponse() throws SQLException {
        Product product = new Product();
        product.setId(1);
        product.setProductName("Test Product");

        Blob mockProductImage = mock(Blob.class);
        Blob mockQrCode = mock(Blob.class);
        product.setProductImage(mockProductImage);
        product.setQrCode(mockQrCode);

        when(mockProductImage.getBytes(1, (int) mockProductImage.length())).thenReturn(new byte[]{1, 2, 3});
        when(mockQrCode.getBytes(1, (int) mockQrCode.length())).thenReturn(new byte[]{4, 5, 6});

        ProductResponse result = orderController.getProductResponse(product);

        // Assert
        assertEquals(1, result.getId());
        assertEquals("Test Product", result.getProductName());
        assertEquals(Base64.encodeBase64String(new byte[]{1, 2, 3}),result.getProductImage());
        assertEquals(Base64.encodeBase64String(new byte[]{4, 5, 6}),result.getQrCode());
    }

    @Test
    void getProductResponse_ExceptionThrown_ReturnsPhotoRetrievaException() throws SQLException {
        // Arrange
        Product product = new Product();
        product.setProductImage(mock(Blob.class));
        product.setQrCode(mock(Blob.class));
        when(product.getProductImage().getBytes(1, (int) product.getProductImage().length())).thenThrow(SQLException.class);

        // Act & Assert
        assertThrows(PhotoRetrievaException.class, () -> orderController.getProductResponse(product));
    }


    @Test
    void parseDate_Success(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date date=null;
        try {
            date = dateFormat.parse("2024-01-01T00:00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Date result=orderController.parseDate("2024-01-01T00:00");
        assertEquals(date,result);
    }

    @Test
    void parseDate_Failure(){
        Date result=orderController.parseDate("2099-01-01");
        assertNull(result);
    }
}
