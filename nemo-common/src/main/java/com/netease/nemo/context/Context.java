package com.netease.nemo.context;

import com.google.common.collect.ImmutableMap;
import com.netease.nemo.openApi.dto.neroom.NeRoomMemberDto;
import com.netease.nemo.util.UUIDUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class Context {
    private static ThreadLocal<Context> threadLocal = new ThreadLocal<>();
    private String userUuid;
    private long startTime;
    private String traceId;
    private String reqBody;
    private String clientIp;
    private String deviceId;
    private String imVersion;
    private String rtcVersion;
    private String roomKitVersion;
    private String appKey;
    private String userToken;
    private ImmutableMap<String, String> headerMap;
    private String respMsg;
    private Object respBody;

    public static Context get() {
        return threadLocal.get();
    }

    public static Context init(String traceId) {
        Context context = new Context();
        if (StringUtils.isEmpty(traceId)) {
            traceId = UUIDUtil.getUUID();
        }
        context.startTime = System.currentTimeMillis();
        context.traceId = traceId;

        threadLocal.set(context);
        return context;
    }

    public void unload() {
        threadLocal.remove();
    }

    public void setApiParam() {
        if (headerMap == null || headerMap.size() == 0) {
            return;
        }
        if (!StringUtils.isEmpty(headerMap.get("user".toLowerCase()))) {
            this.userUuid = headerMap.get("user".toLowerCase());
        }
        this.deviceId = StringUtils.isEmpty(headerMap.get("deviceId".toLowerCase())) ? "unknown" : headerMap.get("deviceId".toLowerCase());
        if (!StringUtils.isEmpty(headerMap.get("token"))) {
            this.userToken = headerMap.get("token");
        }

        if (!StringUtils.isEmpty(headerMap.get("appkey".toLowerCase()))) {
            this.appKey = headerMap.get("appkey".toLowerCase());
        }
    }
}
