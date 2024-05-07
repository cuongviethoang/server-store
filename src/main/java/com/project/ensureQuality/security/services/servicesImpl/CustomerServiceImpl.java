package com.project.ensureQuality.security.services.servicesImpl;

import com.project.ensureQuality.model.Customer;
import com.project.ensureQuality.model.Order;
import com.project.ensureQuality.payload.response.CustomerResponse;
import com.project.ensureQuality.payload.response.MessageResponse;
import com.project.ensureQuality.payload.response.PaginationOrderResponse;
import com.project.ensureQuality.repository.CustomerRepository;
import com.project.ensureQuality.security.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public MessageResponse addNewCustomer(Customer newCus) {
        if(customerRepository.existsByPhoneNumber(newCus.getPhoneNumber())) {
            return new MessageResponse("Số điện thoại đã tồn tại", 1);
        }
        customerRepository.save(newCus);
        return new MessageResponse("Tạo khách hàng thành công", 0);
    }

    @Override
    public MessageResponse updateCustomer(Customer updateCus) {
        Optional<Customer> customer = customerRepository.findById(updateCus.getId());
        if(!customer.isEmpty()) {
            // kiểm tra khi update có thay đổi số điện thoại không
            if(!customer.get().getPhoneNumber().equals(updateCus.getPhoneNumber())) {
                if(customerRepository.existsByPhoneNumber(updateCus.getPhoneNumber())) {
                    return new MessageResponse("Số điện thoại đã tồn tại", 1);
                }
            }
            customerRepository.save(updateCus);
            return new MessageResponse("Cập nhật thông tin khách hàng thành công", 0);
        }
        return new MessageResponse("Thất bại, có vẻ khách hàng đã bị xóa trong hệ thống", 1);
    }

    @Override
    public List<Customer> getAllCustomer(int page, int limit) {
        int offset = (page-1) * limit;
        List<Customer> customers = customerRepository.getCustomersWithPagination(offset, limit);
        return customers;
    }

    @Override
    public Customer getDetailCustomer(int cusId) {
        return customerRepository.findById(cusId).orElseThrow(() -> new RuntimeException("Không thể tải chi tiết người dùng, vui lòng thử lại"));
    }

    @Override
    public int getAllCusNum() {
        List<Customer> customers = customerRepository.findAll();
        return customers.size();
    }

    @Override
    public CustomerResponse getListCusWhenSearch(String q, int currentPage) {
        List<Customer> customers = customerRepository.getListCusByValueSearch(q);
        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setTotal_cus(customers.size());
        customerResponse.setCurrent_page(currentPage);
        customerResponse.setPer_page(5);
        int total_pages = (int) Math.ceil((double) customers.size() / 5);
        customerResponse.setTotal_page(total_pages);
        if(currentPage * 5 > customers.size()) {
            customerResponse.setData(customers.subList((currentPage-1)*5, customers.size()));
        } else {
            customerResponse.setData(customers.subList((currentPage-1)*5, currentPage*5));
        }
        return customerResponse;
    }

    @Override
    public PaginationOrderResponse getAllOrdersOfCus(int cusId, int limit) {
        Customer customer = customerRepository.findById(cusId).get();
        List<Order> orders = customer.getOrders();
        List<Order> customOrders = new ArrayList<>();
        for(Order o : orders) {
            Order newOrder = new Order();
            newOrder.setId(o.getId());
            newOrder.setCreateTime(o.getCreateTime());
            newOrder.setCustomer(o.getCustomer());
            newOrder.setPayment(o.getPayment());
            newOrder.setCode(o.getCode());
            newOrder.setUser(o.getUser());
            customOrders.add(newOrder);
        }
        PaginationOrderResponse paginationOrderResponse = new PaginationOrderResponse();
        paginationOrderResponse.setTotal_order(customOrders.size());
        paginationOrderResponse.setCurrent_page(limit);
        paginationOrderResponse.setPer_page(8);

        int total_pages = (int) Math.ceil((double) customOrders.size() / 8);
        paginationOrderResponse.setTotal_page(total_pages);
        if(limit * 8 > customOrders.size()) {
            paginationOrderResponse.setData(customOrders.subList((limit-1)*5, customOrders.size()));
        } else {
            paginationOrderResponse.setData(customOrders.subList((limit-1)*5, limit*5));
        }
        return paginationOrderResponse;
    }
}
