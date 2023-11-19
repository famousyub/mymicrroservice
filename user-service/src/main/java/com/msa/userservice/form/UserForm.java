package com.msa.userservice.form;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserForm {

    @NotNull(message = "아이디를 입력해주세요")
    @Size(min = 2, message = "2글자 이상 입력해주세요")
    private String username;

    @NotNull(message = "비밀번호를 입력해주세요")
    @Size(min = 4, message = "4글자 이상 입력해주세요")
    private String password;

    @NotNull(message = "이름을 입력해주세요")
    @Size(min = 2, message = "2글자 이상 입력해주세요")
    private String name;
}
