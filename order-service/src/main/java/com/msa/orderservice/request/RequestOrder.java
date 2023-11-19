package com.msa.orderservice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RequestOrder {
    private Long productId;
    private String name;
    private int count;
    private int price;
}
