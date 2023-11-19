package com.msa.userservice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseOrder {

    private Long id;
    private String name;
    private int price;
    private int count;
    private int totalPrice;
    private LocalDateTime createdAt;
}