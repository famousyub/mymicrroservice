package com.msa.orderservice;
import com.msa.orderservice.kafka.KafkaProducer;
import com.msa.orderservice.request.RequestOrder;
import com.msa.orderservice.response.ResponseOrder;
import com.msa.orderservice.response.ResponseOrderComp;
import com.msa.orderservice.response.enums.Code;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class OrderController {

    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;

    @GetMapping("/status-check")
    public String status(HttpServletRequest request) {
        return "order-service 작동중 [PORT:" + request.getServerPort() + "]";
    }

    /**
     * 주문 하기
     * @param userId
     * @param requestOrder
     * @return
     */
    @PostMapping("/{userId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseOrderComp createOrder(@PathVariable Long userId,
                                      @RequestBody RequestOrder requestOrder) {
        ResponseOrder order = orderService.createOrder(requestOrder, userId);

        //kafka topic 에 메세지 전달
        kafkaProducer.send("example-product-topic", order);

        return ResponseOrderComp.builder()
                        .code(Code.SUCCESS)
                        .status(HttpStatus.CREATED.value())
                        .message("주문 완료")
                        .order(order)
                        .build();
    }

    /**
     * 내 주문 확인
     * @param userId
     * @return
     */
    @GetMapping("/{userId}/orders")
    public ResponseEntity getMyOrder(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }
}
