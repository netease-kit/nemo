package com.netease.nemo.config;

import com.netease.nemo.handler.RestResponseReturnValueHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestResponseBodyConfiguration {
    @Resource
    private RequestMappingHandlerAdapter adapter;

    @PostConstruct
    public void injectSelfReturnValueHandler() {
        List<HandlerMethodReturnValueHandler> newReturnValueHandlerList = new ArrayList<>();
        List<HandlerMethodReturnValueHandler> returnValueHandlerList = adapter.getReturnValueHandlers();
        if (returnValueHandlerList != null) {
            newReturnValueHandlerList.add(new RestResponseReturnValueHandler());
            newReturnValueHandlerList.addAll(returnValueHandlerList);
            adapter.setReturnValueHandlers(newReturnValueHandlerList);
        }
    }
}
