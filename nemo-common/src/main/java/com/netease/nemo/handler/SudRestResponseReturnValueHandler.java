package com.netease.nemo.handler;

import com.netease.nemo.annotation.SudRestResponseBody;
import com.netease.nemo.result.SudResultView;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class SudRestResponseReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        SudRestResponseBody ann = methodParameter.getContainingClass().getAnnotation(SudRestResponseBody.class);
        if (ann != null) {
            return true;
        }
        return methodParameter.hasMethodAnnotation(SudRestResponseBody.class);
    }

    @Override
    public void handleReturnValue(Object o, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) {
        modelAndViewContainer.setView(SudResultView.success(o));
    }
}
