package com.project.ensureQuality.payload.response;

import com.project.ensureQuality.model.Customer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CustomerResponse {
    private int total_cus;
    private int current_page;
    private int per_page;
    private int  total_page;
    private List<Customer> data;



    public CustomerResponse(int total_cus, int current_page,int per_page, int total_page, List<Customer> data ) {
        this.total_cus = total_cus;
        this.current_page = current_page;
        this.per_page = per_page;
        this.total_page = total_page;
        this.data = data;
    }


}
