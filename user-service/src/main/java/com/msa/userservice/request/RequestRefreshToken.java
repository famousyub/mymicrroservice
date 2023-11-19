package com.msa.userservice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestRefreshToken {
    @NotNull(message = "grantType 을 입력해주세요")
    private String grantType;
    @NotNull(message = "refreshToken 을 입력홰주세요")
    private String refreshToken;
}
