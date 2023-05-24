package com.netease.nemo.handler;

import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.result.ResultView;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class RestResponseReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        RestResponseBody ann = methodParameter.getContainingClass().getAnnotation(RestResponseBody.class);
        if (ann != null) {
            return true;
        }
        return methodParameter.hasMethodAnnotation(RestResponseBody.class);
    }

    @Override
    public void handleReturnValue(Object o, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) {
        modelAndViewContainer.setView(ResultView.success(o));
    }
}
