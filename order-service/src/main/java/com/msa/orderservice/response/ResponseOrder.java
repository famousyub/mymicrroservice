package com.msa.orderservice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseOrder {

    private Long id;
    private String name;
    private int price;
    private int count;
    private int totalPrice;
    private LocalDateTime createdAt;
    private Long productId;
    private Long userId;
}