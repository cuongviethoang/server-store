package com.project.ensureQuality.payload.response;

import com.project.ensureQuality.model.ItemOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationItemOrderResponse {

    private int total_item;
    private int per_page;
    private int total_page;
    private int current_page;
    private List<ItemOrder> data;
}
