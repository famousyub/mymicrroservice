package com.msa.userservice.config;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4JConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(4)    //10번중에 4번 실패하면 CircuitBreaker OPEN (default : 5)
                .waitDurationInOpenState(Duration.ofMillis(1000))   //CircuitBreaker 를 OPEN 한 상태를 유지하는 지속시간(1초로 설정, default : 1분)
                                                                    //1초 이후로는 CircuitBreaker 가 CLOSED 되어 다시 다른 마이크로서비스에 접속 가능
                //CircuitBreaker 가 CLOSED 될 때 결과값을 저장하기 위해 count or time 어떤걸 사용할지
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(2)
                .build();

        //TimeLimiter 는 supplier 가 얼마나 오류가 생겼을 때 이것을 오류로 간주하여 CircuitBreaker 를 OPEN 할지
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(4)) //4초동안 응답이 없을 때 CircuitBreaker OPEN
                .build();

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build());
    }
}
