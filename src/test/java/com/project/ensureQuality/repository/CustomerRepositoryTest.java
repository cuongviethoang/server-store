package com.project.ensureQuality.repository;

import com.project.ensureQuality.model.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void testGetMethod_WhenGetCustomersPagination_ReturnCustomersPagination() {
        int offset = 0;
        int limit = 10;

        // When
        List<Customer> result = customerRepository.getCustomersWithPagination(offset, limit);

        // Then
        Assertions.assertEquals(10, result.size()); // Kiểm tra số lượng khách hàng trả về
        // Có thể kiểm tra thêm logic phức tạp hơn ở đây nếu cần
    }

    @Test
    public void testGetMathod_WhenSearchCustomersPagination_ReturnCustomersByValueSearch() {
        // Given
        String searchKeyword = "anh";
        List<Customer> result = customerRepository.getListCusByValueSearch(searchKeyword);
        Assertions.assertEquals(1, result.size()); // Kiểm tra số lượng khách hàng trả về
    }
}
