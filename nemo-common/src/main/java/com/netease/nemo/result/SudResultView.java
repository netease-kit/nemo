package com.netease.nemo.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netease.nemo.context.Context;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 忽然游戏返回对象
 *
 * @Author：CH
 * @Date：2023/8/14 10:30 AM
 */
@Data
@Slf4j
public class SudResultView<T> implements View {

    public static final int DEFAULT_OK = 0;

    /**
     * 响应码
     */
    @JsonProperty("ret_code")
    private int retCode;

    /**
     * 响应消息
     */
    @JsonProperty("ret_msg")
    private String retMsg;

    /**
     * 业务响应数据
     */
    @JsonProperty("data")
    private T data;

    /**
     * sdk错误码
     */
    @JsonProperty("sdk_error_code")
    private Integer sdkErrorCode;

    public void setRetCode(SudRetCodeEnum retCode) {
        this.retCode = retCode.getIndex();
        this.retMsg = retCode.getName();
    }

    public SudResultView() {
    }

    public SudResultView(T data) {
        this();
        this.retCode = DEFAULT_OK;
        this.data = data;
    }

    public SudResultView(int code, String msg) {
        this();
        this.retCode = code;
        this.retMsg = msg;
    }

    public SudResultView(int code, int sdkErrorCode, String msg) {
        this();
        this.retCode = code;
        this.retMsg = msg;
        this.sdkErrorCode = sdkErrorCode;
    }

    public static SudResultView failed(int code, int sdkErrorCode, String msg) {
        return new SudResultView(code, sdkErrorCode, msg);
    }

    public static SudResultView success(Object data) {
        return new SudResultView(data);
    }

    @Override
    public String getContentType() {
        return View.super.getContentType();
    }

    @Override
    public void render(Map<String, ?> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String costTime = (System.currentTimeMillis() - Context.get().getStartTime()) + "ms";

        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        if (retCode == DEFAULT_OK) {
            returnMap.put("ret_code", retCode);
            if (data != null) {
                returnMap.put("data", data);
            }
        } else {
            returnMap.put("ret_code", retCode);
            returnMap.put("ret_msg", retMsg);
            returnMap.put("sdk_error_code", sdkErrorCode);
        }
        returnMap.put("requestId", Context.get().getTraceId());
        returnMap.put("costTime", costTime);

        Context.get().setRespBody(returnMap);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            response.getOutputStream().write(GsonUtil.toJsonNoDouble(returnMap).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Handler error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public static enum SudRetCodeEnum {
        /**
         * 成功
         */
        SUCCESS(0, "成功"),

        /**
         * 失败
         */
        REQUEST_FAILED(1, "失败");
        @Getter
        private final String name;

        @Getter
        private final int index;

        SudRetCodeEnum(int index, String name) {
            this.name = name;
            this.index = index;
        }
    }
}
