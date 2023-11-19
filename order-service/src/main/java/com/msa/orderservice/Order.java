package com.msa.orderservice;
import com.msa.orderservice.request.RequestOrder;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int count;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long userId;

    /***
     * 주문
     */
    public static Order createOrder(RequestOrder dto, Long userId) {
        return Order.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .count(dto.getCount())
                .totalPrice(dto.getPrice() * dto.getCount())
                .createdAt(LocalDateTime.now())
                .productId(dto.getProductId())
                .userId(userId)
                .build();
    }

    @Builder
    public Order(String name, int count, int price, int totalPrice, LocalDateTime createdAt, Long productId, Long userId) {
        this.name = name;
        this.count = count;
        this.price = price;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.productId = productId;
        this.userId = userId;
    }

}
