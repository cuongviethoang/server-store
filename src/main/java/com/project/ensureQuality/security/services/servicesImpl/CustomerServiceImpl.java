package com.project.ensureQuality.security.services.servicesImpl;

import com.project.ensureQuality.model.Customer;
import com.project.ensureQuality.payload.response.MessageResponse;
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
            return new MessageResponse("Cập nhật thông tin khách hàng thất bại", 1);
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
}
