package com.msa.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProductDto {

    private Long productId;
    private String name;
    private int price;
    private int totalPrice;

    private Long orderId;
    private Long userId;
}
