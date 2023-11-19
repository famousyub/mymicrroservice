package com.msa.userservice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestLogin {
    @NotNull(message = "아이디를 입력해주세요")
    private String username;
    @NotNull(message = "비밀번호를 입력해주세요")
    private String password;
}
