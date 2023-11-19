package com.msa.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class OrderDto {

    private Long id;
    private String name;
    private int count;
    private int price;
    private int totalPrice;
    private LocalDateTime createAt;

}
