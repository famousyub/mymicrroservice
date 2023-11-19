package com.msa.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.orderservice.request.RequestOrder;
import com.msa.orderservice.response.ResponseOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    OrderService orderService;

    @Test
    @DisplayName("주문 생성 성공 테스트")
    public void createOrder() throws Exception {
        //given
        RequestOrder dto = RequestOrder.builder()
                .productId(1L)
                .name("상품1")
                .price(1000)
                .count(10)
                .build();
        //when
        ResultActions resultActions = requestCreateOrder(dto, 2L);
        //then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("code").value("SUCCESS"))
                .andExpect(jsonPath("message").value("주문 완료"))
                .andExpect(jsonPath("order.id").exists())
                .andExpect(jsonPath("order.name").value(dto.getName()))
                .andExpect(jsonPath("order.price").value(dto.getPrice()))
                .andExpect(jsonPath("order.count").value(dto.getCount()))
                .andExpect(jsonPath("order.createdAt").exists());
    }

    @Test
    @DisplayName("나의 주문 목록 조회테스트")
    public void findMyOrders() throws Exception {
        //given
        long userId = 1L;
        RequestOrder dto1 = RequestOrder.builder()
                .productId(userId)
                .name("상품1")
                .price(1000)
                .count(10)
                .build();
        RequestOrder dto2 = RequestOrder.builder()
                .productId(2L)
                .name("상품2")
                .price(2000)
                .count(20)
                .build();
        //when
        ResponseOrder savedOrder0 = orderService.createOrder(dto1, userId);
        ResponseOrder savedOrder1 = orderService.createOrder(dto2, userId);
        ResultActions resultActions = requestFindMyOrders(userId);
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(savedOrder0.getId()))
                .andExpect(jsonPath("[0].name").value(savedOrder0.getName()))
                .andExpect(jsonPath("[0].price").value(savedOrder0.getPrice()))
                .andExpect(jsonPath("[0].count").value(savedOrder0.getCount()))
                .andExpect(jsonPath("[0].totalPrice").value(savedOrder0.getTotalPrice()))
                .andExpect(jsonPath("[0].createdAt").exists())
                .andExpect(jsonPath("[1].id").value(savedOrder1.getId()))
                .andExpect(jsonPath("[1].name").value(savedOrder1.getName()))
                .andExpect(jsonPath("[1].price").value(savedOrder1.getPrice()))
                .andExpect(jsonPath("[1].count").value(savedOrder1.getCount()))
                .andExpect(jsonPath("[1].totalPrice").value(savedOrder1.getTotalPrice()))
                .andExpect(jsonPath("[1].createdAt").exists());
    }

    /**
     * 주문 요청
     * @param dto
     */
    private ResultActions requestCreateOrder(RequestOrder dto, Long userId) throws Exception {
        return mockMvc.perform(post("/{userId}/orders", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print());
    }

    /**
     * 주문 목록 보기 요청
     * @param userId
     */
    private ResultActions requestFindMyOrders(Long userId) throws Exception{
        return mockMvc.perform(get("/{userId}/orders", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }


}