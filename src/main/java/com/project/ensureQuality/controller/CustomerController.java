package com.project.ensureQuality.controller;

import com.project.ensureQuality.model.Customer;
import com.project.ensureQuality.payload.response.CustomerResponse;
import com.project.ensureQuality.payload.response.MessageResponse;
import com.project.ensureQuality.security.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping("/customer/create")
    public ResponseEntity<?> addNewCustomer(@RequestBody Customer customer) {
        try {
            if(customer.getPhoneNumber() == "" || customer.getPhoneNumber() == null){
                return ResponseEntity.status(400).body(new MessageResponse("Lỗi: Số điện thoại là bắt buộc", 1));
            }

            MessageResponse message = customerService.addNewCustomer(customer);
            if(message.getEC() == 0){
                return ResponseEntity.status(200).body(message);
            }

            return ResponseEntity.status(400).body(message);

        } catch (Exception e){
            System.out.println(e);
            return ResponseEntity.status(500).body(new MessageResponse("Lỗi: Error server", -1));
        }
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping("/customer/read")
    public ResponseEntity<?> getAllCustomer(@RequestParam int page, @RequestParam int limit) {
        try {
            List<Customer> customers = customerService.getAllCustomer(page, limit);
            return ResponseEntity.status(200).body(customers);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error server", -1));
        }
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping("/customer/{cusId}")
    public ResponseEntity<?> getDetailCustomer(@PathVariable(value = "cusId") int cusId) {
        try {
            Customer customer = customerService.getDetailCustomer(cusId);
            return  ResponseEntity.status(200).body(customer);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error server", -1));
        }
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping("/customer/read-all-number")
    public ResponseEntity<?> getAllCusNum(){
        try {
            int count = customerService.getAllCusNum();
            return ResponseEntity.status(200).body(count);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Không tải đợc số lượng khách hàng", -1));
        }
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PutMapping("/customer/update")
    public ResponseEntity<?> updateCustomer(@RequestBody Customer customer){
        try {
            if(customer.getPhoneNumber() == "" || customer.getPhoneNumber() == null){
                return ResponseEntity.status(400).body(new MessageResponse("Lỗi: Số điện thoại là bắt buộc", 1));
            }

            MessageResponse message = customerService.updateCustomer(customer);
            if(message.getEC() == 0){
                return ResponseEntity.status(200).body(message);
            }

            return ResponseEntity.status(400).body(message);
        } catch (Exception e){
            System.out.println(e);
            return ResponseEntity.status(500).body(new MessageResponse("Lỗi: Error server",-1));
        }
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping("/customer/search")
    public ResponseEntity<?> getSearchListCustomer(@RequestParam String q, @RequestParam int currentPage) {
        try {
            CustomerResponse customerResponse = customerService.getListCusWhenSearch(q, currentPage);

            return ResponseEntity.status(200).body(customerResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Lỗi server", -1));
        }
    }
}
