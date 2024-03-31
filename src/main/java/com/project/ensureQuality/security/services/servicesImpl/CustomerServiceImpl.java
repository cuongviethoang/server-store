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
        try{
            Optional<Customer> customer = customerRepository.findById(updateCus.getId());
            if(!customer.isEmpty()) {
                if(!customer.get().getPhoneNumber().equals(updateCus.getPhoneNumber())) {
                    if(customerRepository.existsByPhoneNumber(updateCus.getPhoneNumber())) {
                        return new MessageResponse("Số điện thoại đã tồn tại", 1);
                    }
                }
                customerRepository.save(updateCus);
                return new MessageResponse("Cập nhật thông tin khách hàng thành công", 0);
            }
            return new MessageResponse("Thất bại, có vẻ khách hàng đã bị xóa trong hệ thống", 1);
        } catch (Exception e) {
            return new MessageResponse("Không tìm thấy thong tin khách hàng cần cập nhật", 1);
        }
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
        try {
            List<Customer> customers = customerRepository.findAll();
            return customers.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public CustomerResponse getListCusWhenSearch(String q, int currentPage) {
        try {
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
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public PaginationOrderResponse getAllOrdersOfCus(int cusId, int limit) {
        try {
            Customer customer = customerRepository.findById(cusId).get();

            List<Order> orders = customer.getOrders();
            PaginationOrderResponse paginationOrderResponse = new PaginationOrderResponse();
            paginationOrderResponse.setTotal_order(orders.size());
            paginationOrderResponse.setCurrent_page(limit);
            paginationOrderResponse.setPer_page(8);

            int total_pages = (int) Math.ceil((double) orders.size() / 8);
            paginationOrderResponse.setTotal_page(total_pages);
            if(limit * 8 > orders.size()) {
                paginationOrderResponse.setData(orders.subList((limit-1)*5, orders.size()));
            } else {
                paginationOrderResponse.setData(orders.subList((limit-1)*5, limit*5));
            }
            return paginationOrderResponse;
        } catch (Exception e) {
            return null;
        }
    }
}
