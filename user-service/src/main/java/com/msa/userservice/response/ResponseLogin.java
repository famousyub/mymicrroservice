package com.msa.userservice.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResponseLogin {
    private int status;
    private String message;
    private String accessToken;
    private LocalDateTime expiredAt;          //만료 시간
    private String refreshToken;
    private LocalDateTime issuedAt;           //발급 시간
}
