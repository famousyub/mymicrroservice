package com.msa.orderservice.kafka;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.orderservice.response.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ResponseOrder send(String topic, ResponseOrder responseOrder) {
        String json = "";
        try {
            json = objectMapper.writeValueAsString(responseOrder);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        kafkaTemplate.send(topic, json);
        log.info("Kafka Producer 가 order-service 에 data 를 보냈습니다 " + responseOrder);

        return responseOrder;
    }
}
