package com.msa.userservice.error;

import com.msa.userservice.exception.InvalidRefreshTokenException;
import com.msa.userservice.response.Response;
import com.msa.userservice.response.enums.Code;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ExceptionController {

    /**
     * 아이디 중복 오류
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity duplicateUsername(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Response.builder()
                .code(Code.FAIL)
                .status(HttpStatus.CONFLICT.value())
                .message(e.getMessage())
                .build());
    }

    /**
     * 로그인 실패
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity badCredentials(Exception e) {
        Response response = Response.builder()
                .code(Code.FAIL)
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("로그인 실패")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * refreshToken 토큰 값 오류
     */
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity invalidRefreshToken(Exception e) {
        Response response = Response.builder()
                .code(Code.FAIL)
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * getOrder 오류
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity getOrderError(Exception e) {
        Response response = Response.builder()
                .code(Code.FAIL)
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}
