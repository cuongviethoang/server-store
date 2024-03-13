package com.project.ensureQuality.security.services;

import com.project.ensureQuality.model.Customer;
import com.project.ensureQuality.payload.response.MessageResponse;

import java.util.List;

public interface CustomerService {

    MessageResponse addNewCustomer(Customer newCus);

    MessageResponse updateCustomer(Customer updateCus);

    List<Customer> getAllCustomer(int page, int limit);

    Customer getDetailCustomer(int cusId);
}
