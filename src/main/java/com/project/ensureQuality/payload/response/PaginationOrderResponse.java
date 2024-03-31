package com.project.ensureQuality.payload.response;

import com.project.ensureQuality.model.Order;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PaginationOrderResponse {

    private int total_order;
    private int current_page;
    private int per_page;
    private int  total_page;
    private List<Order> data;



    public PaginationOrderResponse(int total_order, int current_page,int per_page, int total_page, List<Order> data ) {
        this.total_order = total_order;
        this.current_page = current_page;
        this.per_page = per_page;
        this.total_page = total_page;
        this.data = data;
    }
}
