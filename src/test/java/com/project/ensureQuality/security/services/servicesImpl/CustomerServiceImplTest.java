package com.project.ensureQuality.security.services.servicesImpl;

import com.project.ensureQuality.model.Customer;
import com.project.ensureQuality.model.Order;
import com.project.ensureQuality.payload.response.CustomerResponse;
import com.project.ensureQuality.payload.response.MessageResponse;
import com.project.ensureQuality.payload.response.PaginationOrderResponse;
import com.project.ensureQuality.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPostMethod_WhenAddCustomer_ReturnNewCustomer() {
        Customer newCustomer = new Customer();
        newCustomer.setPhoneNumber("1234567890");

        when(customerRepository.existsByPhoneNumber(eq("1234567890"))).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        MessageResponse response = customerService.addNewCustomer(newCustomer);

        assertEquals("Tạo khách hàng thành công", response.getEM());
        assertEquals(0, response.getEC());
    }

    @Test
    public void testPostMethod_WhenAddCustomer_ReturnExistPhoneNumber() {
        Customer existingCustomer = new Customer();
        existingCustomer.setPhoneNumber("1234567890");

        when(customerRepository.existsByPhoneNumber(eq("1234567890"))).thenReturn(true);

        MessageResponse response = customerService.addNewCustomer(existingCustomer);

        assertEquals("Số điện thoại đã tồn tại", response.getEM());
        assertEquals(1, response.getEC());
    }

    @Test
    public void testGetMethod_WhenGetAllCustomers_ReturnAllCustomers() {
        List<Customer> customers = getCustomersWithPagination();
        int page = 1;
        int limit = 10;

        when(customerRepository.getCustomersWithPagination(eq(0), eq(10))).thenReturn(customers);

        List<Customer> result = customerService.getAllCustomer(page, limit);
        assertEquals(customers, result);
    }

    private List<Customer> getCustomersWithPagination() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer(1,"A", "0123456789"));
        customers.add(new Customer(2,"B", "0123456788"));
        customers.add(new Customer(3,"C", "0123456787"));
        customers.add(new Customer(4,"D", "0123456786"));
        customers.add(new Customer(5,"E", "0123456785"));
        return customers;
    }

    @Test
    public void testPutMethod_WhenUpdateCustomer_ReturnUpdateCustomer() {
        // Tạo một đối tượng Customer cần cập nhật
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1); // Thiết lập ID của khách hàng hiện có
        existingCustomer.setPhoneNumber("1234567890");

        // Mock behavior của CustomerRepository
        when(customerRepository.findById(eq(1))).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByPhoneNumber(eq("1234567890"))).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);

        // Gọi phương thức cần test
        MessageResponse response = customerService.updateCustomer(existingCustomer);

        // Kiểm tra kết quả trả về
        assertEquals("Cập nhật thông tin khách hàng thành công", response.getEM());
        assertEquals(0, response.getEC());

        // Đảm bảo rằng phương thức save đã được gọi với đối tượng Customer được cập nhật
        verify(customerRepository, times(1)).save(eq(existingCustomer));
    }

    @Test
    public void testPutMethod_WhenUpdateCustomer_ReturnPhoneNumberAlreadyExists() {
        // Mock data
        Customer existingCustomer = new Customer(1, "John Doe", "1234567890");
        Customer updatedCustomer = new Customer(1, "John Doe", "9876543210");

        // Mock behavior của customerRepository.findById()
        when(customerRepository.findById(updatedCustomer.getId())).thenReturn(Optional.of(existingCustomer));

        // Mock behavior của customerRepository.existsByPhoneNumber()
        when(customerRepository.existsByPhoneNumber(updatedCustomer.getPhoneNumber())).thenReturn(true);

        // Gọi phương thức cần test
        MessageResponse result = customerService.updateCustomer(updatedCustomer);

        // Kiểm tra kết quả trả về
        assertEquals("Số điện thoại đã tồn tại", result.getEM());
        assertEquals(1, result.getEC());

        // Verify không gọi phương thức save của customerRepository
        verify(customerRepository, never()).save(updatedCustomer);
    }

    @Test
    public void testPutMethod_WhenUpdateCustomer_ReturnCustomerNotFound() {
        Customer nonExistingCustomer = new Customer();
        nonExistingCustomer.setId(2); // Thiết lập ID của khách hàng không tồn tại

        // Mock behavior của CustomerRepository
        when(customerRepository.findById(eq(2))).thenReturn(Optional.empty());

        MessageResponse response = customerService.updateCustomer(nonExistingCustomer);

        assertEquals("Thất bại, có vẻ khách hàng đã bị xóa trong hệ thống", response.getEM());
        assertEquals(1, response.getEC());

        // Đảm bảo rằng phương thức save không được gọi nếu khách hàng không tồn tại
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    public void testGetMethod_WhenGetDetailCustomer_ReturnCustomer() {
        // Khách hàng có ID tồn tại trong cơ sở dữ liệu
        int customerId = 1;
        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setUsername("John Doe");

        // Mock behavior của CustomerRepository
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        // Gọi phương thức cần test
        Customer result = customerService.getDetailCustomer(customerId);

        // Kiểm tra kết quả trả về
        assertEquals(existingCustomer.getId(), result.getId());
        assertEquals(existingCustomer.getUsername(), result.getUsername());
    }

    @Test
    public void testGetMethod_WhenGetDetailCustomer_ReturnCustomerNotFound() {
        // Khách hàng không tồn tại trong cơ sở dữ liệu
        int customerId = 2;

        // Mock behavior của CustomerRepository
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Kiểm tra xem phương thức có ném ra RuntimeException không khi không tìm thấy khách hàng
        assertThrows(RuntimeException.class, () -> {
            customerService.getDetailCustomer(customerId);
        });
    }

    @Test
    public void testGetMethod_WhenTotalCustomers_ReturnTotalCustomers() {
        // Danh sách khách hàng mẫu
        List<Customer> mockCustomers = Arrays.asList(
                new Customer(1, "John Doe", "1234567890"),
                new Customer(2, "Alice Smith", "9876543210"),
                new Customer(3, "Bob Johnson", "555888999")
        );

        // Mock behavior của CustomerRepository.findAll()
        when(customerRepository.findAll()).thenReturn(mockCustomers);

        // Gọi phương thức cần test
        int result = customerService.getAllCusNum();

        // Kiểm tra kết quả trả về
        assertEquals(mockCustomers.size(), result);
    }

    @Test
    public void testGetMethod_WhenGetListCustomersWhenSearch_ReturnListCustomers() {
        // Giá trị tìm kiếm và trang hiện tại
        String searchValue = "Doe";
        int currentPage = 1;

        // Danh sách khách hàng mẫu dựa trên giá trị tìm kiếm
        List<Customer> mockCustomers = Arrays.asList(
                new Customer(1, "John Doe", "1234567890"),
                new Customer(2, "Jane Doe", "9876543210")
        );

        // Giả lập phương thức getListCusByValueSearch của repository trả về danh sách khách hàng mẫu
        when(customerRepository.getListCusByValueSearch(anyString())).thenReturn(mockCustomers);

        // Gọi phương thức cần test
        CustomerResponse result = customerService.getListCusWhenSearch(searchValue, currentPage);

        // Kiểm tra kết quả trả về
        assertEquals(mockCustomers.size(), result.getTotal_cus()); // Số lượng khách hàng
        assertEquals(currentPage, result.getCurrent_page()); // Trang hiện tại
        assertEquals(5, result.getPer_page()); // Số lượng khách hàng trên mỗi trang
        int expectedTotalPages = (int) Math.ceil((double) mockCustomers.size() / 5);
        assertEquals(expectedTotalPages, result.getTotal_page()); // Tổng số trang

        // Kiểm tra dữ liệu trả về trên trang hiện tại
        List<Customer> expectedData;
        int startIndex = (currentPage - 1) * 5;
        int endIndex = Math.min(startIndex + 5, mockCustomers.size());
        expectedData = mockCustomers.subList(startIndex, endIndex);
        assertEquals(expectedData, result.getData());
    }

    @Test
    public void testGetMethod_WhenGetAllOrdersOfCustomer_ReturnListOrders() {
        // Mock data
        int cusId = 1;
        int limit = 2;
        Customer customer = new Customer();
        customer.setId(cusId);
        List<Order> orders = Arrays.asList(
                new Order(1, "ABC123", new Date(), customer, null, null),
                new Order(2, "DEF456", new Date(), customer, null, null),
                new Order(3, "DEF789", new Date(), customer, null, null),
                new Order(4, "DEF012", new Date(), customer, null, null),
                new Order(5, "DEF3456", new Date(), customer, null, null)
        );
        customer.setOrders(orders);

        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        PaginationOrderResponse paginationOrderResponse = customerService.getAllOrdersOfCus(1, 1);

        assertEquals(5, paginationOrderResponse.getTotal_order());
        assertEquals(1, paginationOrderResponse.getCurrent_page());
        assertEquals(8, paginationOrderResponse.getPer_page());
        assertEquals(1, paginationOrderResponse.getTotal_page());
        assertEquals(5, paginationOrderResponse.getData().size());
    }
}
