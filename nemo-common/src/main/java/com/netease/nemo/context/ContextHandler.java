package com.netease.nemo.context;

import com.netease.nemo.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContextHandler {

    public Context init() {
        String traceId = UUIDUtil.getUUID();
        MDC.put("traceId", traceId);
        return Context.init(traceId);
    }

    /**
     * 销毁上下文
     */
    public void destroy() {
        Context.get().unload();
        MDC.clear();
    }
}
