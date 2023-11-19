package com.msa.productservice.response;

import com.msa.productservice.response.enums.Code;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResponseProductEnroll {

    private Code code;
    private int status;
    private String message;
    private ResponseProduct product;
}
