package com.msa.gatewayservice.filter;

import com.msa.gatewayservice.response.Response;
import com.msa.gatewayservice.response.enums.Code;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.security.Key;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory {

    @Value("${jwt.secretKey}") private String secretKey;
    private Key key;
    /**
     * secretKey 암호화 초기화
     */
    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(this.secretKey.getBytes());
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

                if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION))
                    return sendErrorResponse(exchange, "토큰 값이 존재하지 않습니다", HttpStatus.UNAUTHORIZED);

                String header = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                String token = header.substring(7); //"Bearer "

            try{
                jwtInvalid(token);
            } catch (MalformedJwtException e) {
                return sendErrorResponse(exchange, "손상된 토큰입니다", HttpStatus.UNAUTHORIZED);
            } catch (ExpiredJwtException e) {
                return sendErrorResponse(exchange, "만료된 토큰입니다", HttpStatus.UNAUTHORIZED);
            } catch (UnsupportedJwtException e) {
                return sendErrorResponse(exchange, "지원하지 않는 토큰입니다", HttpStatus.UNAUTHORIZED);
            } catch (SignatureException e) {
                return sendErrorResponse(exchange, "시그니처 검증에 실패한 토큰입니다", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        }));
    }

    private void jwtInvalid(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(message);
//        Response jsonResponse = Response.builder()
//                .code(Code.FAIL)
//                .status(HttpStatus.UNAUTHORIZED.value())
//                .message(message)
//                .build();
//        ServerResponse.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE)).body(jsonResponse, Response.class);
        return response.setComplete();
    }
}
