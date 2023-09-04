package com.netease.nemo.result;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.context.Context;
import com.netease.nemo.util.gson.GsonUtil;
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

@Slf4j
public class ResultView implements View {

    public static final int DEFAULT_OK = 200;

    /**
     * default ok message
     */
    public static final String DEFAULT_OK_MESSAGE = "success";

    /**
     * default failed code
     */
    public static final int DEFAULT_FAILED = -1;

    /**
     * response data
     */
    private Object data;

    /**
     * error code
     */
    private int code;

    /**
     * requestId
     */
    private String requestId;

    /**
     * error message
     */
    private String msg;

    public ResultView(){
    }

    public ResultView(int code, String msg){
        this();
        this.code = code;
        this.msg = msg;
    }

    public ResultView(Object data){
        this();
        this.code = ErrorCode.OK;
        this.data = data;
    }

    public static ResultView failed(int code, String msg) {
        return new ResultView(code, msg);
    }

    public static ResultView success(Object data) {
        return new ResultView(data);
    }


    @Override
    public void render(Map<String, ?> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String costTime = (System.currentTimeMillis() - Context.get().getStartTime()) + "ms";

        if(request.getRequestURI().startsWith("/nemo/openAi/arsFile")) {
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setContentLength(((String) data).length());
            response.getWriter().write((String) data);
        }

        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        if (code == DEFAULT_OK) {
            returnMap.put("code", code);
            if (data != null) {
                returnMap.put("data", data);
            }
        } else {
            returnMap.put("code", code);
            returnMap.put("msg", msg);
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
}
