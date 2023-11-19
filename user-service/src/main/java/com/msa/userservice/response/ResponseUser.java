package com.msa.userservice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.msa.userservice.UserRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL) //해당 property 가 null 일 때 json 출력 x
public class ResponseUser {

    private Long id;
    private String username;
    private String name;
    private Set<UserRole> roles;
    private LocalDateTime createdAt;
    private List<ResponseOrder> orders;

}
