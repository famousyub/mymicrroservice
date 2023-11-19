package com.msa.zuulservice.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class ZuulLoggingFilter extends ZuulFilter {

    //사용자의 요청이 들어올 때 마다 먼저 실행되는 메서드
    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //사용자의 요청 uri 로깅
        log.info("[LOG] " + request.getRequestURI());
        return null;
    }

    //라우팅 전에 실행되는 필터
    @Override
    public String filterType() {
        return "pre";
    }

    //여러개의 필터의 순서를 정해주는데 한개밖에 없으므로 1
    @Override
    public int filterOrder() {
        return 1;
    }

    //이 필터의 사용 여부
    @Override
    public boolean shouldFilter() {
        return true;
    }

}
