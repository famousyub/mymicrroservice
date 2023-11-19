package com.msa.gatewayservice.response;
import com.msa.gatewayservice.response.enums.Code;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Response {

    private Code code;
    private int status;
    private String message;

}
