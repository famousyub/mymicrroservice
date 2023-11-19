package com.msa.productservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.productservice.Product;
import com.msa.productservice.ProductRepository;
import com.msa.productservice.exception.NotFoundProductException;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    //example-product-topic 이라는 곳에 데이터가 전달되면 메서드 실행
    @KafkaListener(topics = "example-product-topic")
    public void updateStock(String kafkaMessage) throws NotFoundProductException {
        log.info("Kafka Message : -> " + kafkaMessage);

        //kafka 로 직렬화 했던걸 역직렬화 해서 사용해야된다.
        Map<Object, Object> map = new HashMap<>();
        try {
            //받아온 topic json 값을 Map 형태로 바꿈
            map = objectMapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Long productId = Long.parseLong(map.get("productId").toString());
        int count = Integer.parseInt(map.get("count").toString());

        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundProductException("상품을 찾을 수 없습니다"));
        product.updateStock(count);
        productRepository.save(product);
    }

}
