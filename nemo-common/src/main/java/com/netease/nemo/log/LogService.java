package com.netease.nemo.log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.netease.nemo.context.Context;
import com.netease.nemo.util.gson.GsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogService {
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    public static String LOG_KEY_TRACE = "trace";
    public static String LOG_KEY_HTTP_METHOD = "method";
    public static String LOG_KEY_HTTP_URI = "uri";
    public static String LOG_KEY_HTTP_QUERY_STRING = "queryString";
    public static String LOG_KEY_USER_UUID = "userUuid";
    public static String LOG_KEY_APP_KEY = "appKey";
    public static String LOG_KEY_APP_NAME = "appName";
    public static String LOG_KEY_DEVICE_ID = "deviceId";
    public static String LOG_KEY_CLIENT_TYPE = "clientType";
    public static String LOG_KEY_VERSION_CODE = "versionCode";
    public static String LOG_KEY_COST = "costMs";
    public static String LOG_KEY_CLIENT_IP = "clientIp";
    public static String LOG_KEY_HTTP_RESP_BODY = "respBody";
    public static String LOG_KEY_HTTP_REQ_BODY = "reqBody";
    public static String LOG_KEY_HTTP_BAD_REQ_BODY = "badReqBody";
    public static String LOG_KEY_HTTP_BASE_URI = "baseUri";

    public void exception(Exception ex) {
        if (ex != null) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void logHttp(String httpMethod, String uri, String queryString, String clientIp, String baseUri) {
        if (uri.contains("health")) {
            return;
        }
        try {
            Context context = Context.get();
            JsonObject logJo = new JsonObject();
            logJo.addProperty(LOG_KEY_TRACE, context.getTraceId());
            logJo.addProperty(LOG_KEY_HTTP_METHOD, httpMethod);
            logJo.addProperty(LOG_KEY_HTTP_URI, uri);
            logJo.addProperty(LOG_KEY_HTTP_QUERY_STRING, queryString);
            logJo.addProperty(LOG_KEY_HTTP_BASE_URI, baseUri);
            logJo.addProperty(LOG_KEY_USER_UUID, context.getUserUuid());
            logJo.addProperty(LOG_KEY_APP_KEY, context.getAppKey());
            logJo.addProperty(LOG_KEY_APP_NAME, context.getAppName());
            logJo.addProperty(LOG_KEY_DEVICE_ID, context.getDeviceId());
            logJo.addProperty(LOG_KEY_CLIENT_TYPE, context.getClientType());
            logJo.addProperty(LOG_KEY_VERSION_CODE, context.getVersionCode());

            if (context.getRespMsg() != null) {
                logJo.addProperty("msg", context.getRespMsg());
            }

            logJo.addProperty(LOG_KEY_COST, System.currentTimeMillis() - context.getStartTime());
            logJo.addProperty(LOG_KEY_CLIENT_IP, clientIp);
            logJo.add(LOG_KEY_HTTP_RESP_BODY, context.getRespBody() == null ? null : GsonUtil.toJsonTree(context.getRespBody()));
            try {
                if (StringUtils.isNotEmpty(context.getReqBody())) {
                    JsonElement je = GsonUtil.parseJsonElement(context.getReqBody());
                    logJo.add(LOG_KEY_HTTP_REQ_BODY, je);
                } else {
                    logJo.addProperty(LOG_KEY_HTTP_REQ_BODY, context.getReqBody());
                }
            } catch (Exception e) {
                logJo.addProperty(LOG_KEY_HTTP_REQ_BODY, context.getReqBody());
                logJo.addProperty(LOG_KEY_HTTP_BAD_REQ_BODY, e.getMessage());
            }

            String log = logJo.toString();
            logger.info(log);
        } catch (Exception e) {
            logger.error("接口请求日志打印失败", e);
        }
    }
}
