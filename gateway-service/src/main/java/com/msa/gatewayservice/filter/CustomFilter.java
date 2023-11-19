package com.msa.gatewayservice.filter;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {
    //기본 생성자 필수(Config.class)
    public CustomFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        //Custom Pre Filter 적용
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom PRE FILTER: 요청 id -> {}", request.getId());

            //Custom Post Filter 적용
            return chain.filter(exchange).then(Mono.fromRunnable(() ->{
                //Mono -> Spring Webflux 기능:  동기 방식이 아니라 비동기 방식의 서버를 지원할 때 단일 값 전달 할용 때 사용
                log.info("Custom POST FILTER: 응답 code -> {}", response.getStatusCode());
            }));
        });
    }

    public static class Config {
        //Configuration 정보
    }
}
