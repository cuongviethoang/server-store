package com.project.ensureQuality.controller;

import com.project.ensureQuality.model.Customer;
import com.project.ensureQuality.payload.response.CustomerResponse;
import com.project.ensureQuality.payload.response.MessageResponse;
import com.project.ensureQuality.security.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddNewCustomer_Success() {
        // Mock data
        Customer customer = new Customer();
        customer.setPhoneNumber("1234567890");

        // Mock service response
        MessageResponse successMessage = new MessageResponse("Tạo khách hàng thành công", 0);
        when(customerService.addNewCustomer(customer)).thenReturn(successMessage);

        // Gọi phương thức cần test
        ResponseEntity<?> responseEntity = customerController.addNewCustomer(customer);

        // Kiểm tra HttpStatus và message
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Tạo khách hàng thành công", ((MessageResponse) responseEntity.getBody()).getEM());

        // Verify gọi phương thức addNewCustomer của customerService
        verify(customerService, times(1)).addNewCustomer(customer);
    }

    @Test
    public void testAddNewCustomer_InvalidPhoneNumber() {
        // Mock data with invalid phone number
        Customer customer = new Customer();
        customer.setPhoneNumber(""); // Invalid phone number

        // Gọi phương thức cần test
        ResponseEntity<?> responseEntity = customerController.addNewCustomer(customer);

        // Kiểm tra HttpStatus và message
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Lỗi: Số điện thoại là bắt buộc", ((MessageResponse) responseEntity.getBody()).getEM());

        // Verify không gọi phương thức addNewCustomer của customerService
        verify(customerService, never()).addNewCustomer(customer);
    }

    @Test
    public void testAddNewCustomer_DuplicatePhoneNumber() {
        // Mock data with existing phone number
        Customer customer = new Customer();
        customer.setPhoneNumber("1234567890");

        // Mock service response with error message
        MessageResponse errorMessage = new MessageResponse("Số điện thoại đã tồn tại", 1);
        when(customerService.addNewCustomer(customer)).thenReturn(errorMessage);

        // Gọi phương thức cần test
        ResponseEntity<?> responseEntity = customerController.addNewCustomer(customer);

        // Kiểm tra HttpStatus và message
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Số điện thoại đã tồn tại", ((MessageResponse) responseEntity.getBody()).getEM());

        // Verify gọi phương thức addNewCustomer của customerService
        verify(customerService, times(1)).addNewCustomer(customer);
    }

    @Test
    public void testGetAllCustomer_Success() {
        // Mock data
        int page = 1;
        int limit = 10;
        List<Customer> mockCustomers = new ArrayList<>();
        mockCustomers.add(new Customer(1, "John Doe", "1234567890"));
        mockCustomers.add(new Customer(2, "Alice Smith", "9876543210"));

        // Mock service response
        when(customerService.getAllCustomer(page, limit)).thenReturn(mockCustomers);

        // Gọi phương thức cần test
        ResponseEntity<?> responseEntity = customerController.getAllCustomer(page, limit);

        // Kiểm tra HttpStatus và dữ liệu trả về
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<Customer> returnedCustomers = (List<Customer>) responseEntity.getBody();
        assertEquals(2, returnedCustomers.size()); // Kiểm tra số lượng khách hàng trả về

        // Verify gọi phương thức getAllCustomer của customerService
        verify(customerService, times(1)).getAllCustomer(page, limit);
    }

    @Test
    public void testGetDetailCustomer_Success() {
        // Mock data
        int cusId = 1;
        Customer mockCustomer = new Customer(cusId, "John Doe", "1234567890");

        // Mock service response
        when(customerService.getDetailCustomer(cusId)).thenReturn(mockCustomer);

        // Gọi phương thức cần test
        ResponseEntity<?> responseEntity = customerController.getDetailCustomer(cusId);

        // Kiểm tra HttpStatus và dữ liệu trả về
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockCustomer, responseEntity.getBody());

        // Verify gọi phương thức getDetailCustomer của customerService
        verify(customerService, times(1)).getDetailCustomer(cusId);
    }

    @Test
    public void testGetAllCusNum_Success() {
        // Mock data
        int mockCount = 10;

        // Mock service response
        when(customerService.getAllCusNum()).thenReturn(mockCount);

        // Gọi phương thức cần test
        ResponseEntity<?> responseEntity = customerController.getAllCusNum();

        // Kiểm tra HttpStatus và dữ liệu trả về
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockCount, responseEntity.getBody());

        // Verify gọi phương thức getAllCusNum của customerService
        verify(customerService, times(1)).getAllCusNum();
    }

    @Test
    public void testUpdateCustomer_Success() {
        // Mock data
        Customer customerToUpdate = new Customer(1, "John Doe", "1234567890");

        // Mock service response
        when(customerService.updateCustomer(customerToUpdate)).thenReturn(new MessageResponse("Cập nhật thông tin khách hàng thành công", 0));

        // Gọi phương thức cần test
        ResponseEntity<?> responseEntity = customerController.updateCustomer(customerToUpdate);

        // Kiểm tra HttpStatus và dữ liệu trả về
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Cập nhật thông tin khách hàng thành công", ((MessageResponse) responseEntity.getBody()).getEM());

        // Verify gọi phương thức updateCustomer của customerService
        verify(customerService, times(1)).updateCustomer(customerToUpdate);
    }

    @Test
    public void testUpdateCustomer_InvalidPhoneNumber() {
        // Mock data with invalid phone number
        Customer invalidCustomer = new Customer(1, "John Doe", "");

        // Gọi phương thức cần test
        ResponseEntity<?> responseEntity = customerController.updateCustomer(invalidCustomer);

        // Kiểm tra HttpStatus và message của response
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Lỗi: Số điện thoại là bắt buộc", ((MessageResponse) responseEntity.getBody()).getEM());

        // Không gọi phương thức updateCustomer của customerService vì dữ liệu không hợp lệ
        verify(customerService, never()).updateCustomer(any(Customer.class));
    }

    @Test
    public void testUpdateCustomer_DuplicatePhoneNumber() {
        // Mock data with existing phone number
        Customer customerToUpdate = new Customer();
        customerToUpdate.setPhoneNumber("1234567890");

        // Mock service response with error message
        MessageResponse errorMessage = new MessageResponse("Số điện thoại đã tồn tại", 1);
        when(customerService.updateCustomer(customerToUpdate)).thenReturn(errorMessage);

        // Gọi phương thức cần test
        ResponseEntity<?> responseEntity = customerController.updateCustomer(customerToUpdate);

        // Kiểm tra HttpStatus và message
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Số điện thoại đã tồn tại", ((MessageResponse) responseEntity.getBody()).getEM());

        // Verify gọi phương thức addNewCustomer của customerService
        verify(customerService, times(1)).updateCustomer(customerToUpdate);
    }

    @Test
    public void testGetSearchListCustomer_Success() {
        // Mock data
        String query = "John";
        int currentPage = 1;
        CustomerResponse mockCustomerResponse = new CustomerResponse(10, currentPage, 5, 2, null);

        // Mock service response
        when(customerService.getListCusWhenSearch(query, currentPage)).thenReturn(mockCustomerResponse);

        // Gọi phương thức cần test
        ResponseEntity<?> responseEntity = customerController.getSearchListCustomer(query, currentPage);

        // Kiểm tra HttpStatus và dữ liệu trả về
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockCustomerResponse, responseEntity.getBody());

        // Verify gọi phương thức getListCusWhenSearch của customerService
        verify(customerService, times(1)).getListCusWhenSearch(query, currentPage);
    }
}
