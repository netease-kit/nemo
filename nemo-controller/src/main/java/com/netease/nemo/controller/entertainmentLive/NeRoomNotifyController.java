package com.netease.nemo.controller.entertainmentLive;

import com.google.gson.JsonObject;
import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.context.Context;
import com.netease.nemo.entlive.service.impl.ImEventServiceImpl;
import com.netease.nemo.openApi.enums.ImEventEnum;
import com.netease.nemo.service.NotifyService;
import com.netease.nemo.util.CheckSumBuilder;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequestMapping("/nemo/entertainmentLive/nim/")
@Slf4j
@RestResponseBody
public class NeRoomNotifyController {
    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    @Autowired
    @Qualifier("neRoomNotifyServiceImpl")
    private NotifyService notifyService;

    @Resource
    private ImEventServiceImpl imEventService;
    /**
     * IM和RTC的抄送处理
     *
     * @param request     request
     * @param requestBody 抄送的body数据
     * @return code
     */
    @PostMapping(value = {"notify"}, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public JsonObject notify(HttpServletRequest request, @RequestBody String requestBody) {
        JsonObject result = new JsonObject();
        try {
            if (StringUtils.isEmpty(requestBody)) {
                log.error("NeRoomNotifyController : 抄送内容为空");
                result.addProperty("code", 414);
                return result;
            }
            String curTime = request.getHeader("CurTime");
            String checksum = request.getHeader("CheckSum");

            String verifyMD5 = CheckSumBuilder.getMD5(requestBody);
            String verifyChecksum = CheckSumBuilder.getCheckSum(verifyMD5, curTime, Context.get().getSecret());
            if (verifyChecksum.equals(checksum)) {
                notifyService.handlerNotify(requestBody);
            } else {
                log.error("Bad checksum.");
            }
            result.addProperty("code", 200);
            return result;
        } catch (Exception ex) {
            log.error("Nim Callback Process exception.", ex);
            result.addProperty("code", 414);
            return result;
        }

    }

    /**
     * IM消息抄送
     *
     * @param request
     * @param requestBody
     * @return
     */
    @PostMapping(value = {"/im-event-notify"}, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ImNotifyResponse imEventNotify(HttpServletRequest request, @RequestBody String requestBody) {
        if (StringUtils.isEmpty(requestBody)) {
            log.error("RouteController : 抄送内容为空");
            return new ImNotifyResponse(HttpStatus.BAD_REQUEST.value());
        }
        String appKey = request.getHeader("AppKey");
        if (!yunXinConfigProperties.getAppKey().equals(appKey)) {
            log.info("appkey:{} is not correct! ignore!", appKey);
            return new ImNotifyResponse(HttpStatus.OK.value());
        }
        if (checkSumFailed(request, requestBody)) {
            return new ImNotifyResponse(HttpStatus.BAD_REQUEST.value());
        }
        //验签通过，获取会话信息
        JsonObject jsonObject = GsonUtil.parseJsonObject(requestBody);
        Integer eventType = GsonUtil.getInt(jsonObject, "eventType");
        if(eventType != null){
            ImEventEnum event = ImEventEnum.fromCode(eventType);
            if (Objects.equals(event, ImEventEnum.CHATROOM_INOUT)) {
                imEventService.handlerChatroomInOut(jsonObject);
            }
        }
        return new ImNotifyResponse(HttpStatus.OK.value());
    }

    /**
     * IM消息抄送response
     */
    @Data
    public static class ImNotifyResponse {
        private int code;

        public ImNotifyResponse(int code) {
            this.code = code;
        }
    }

    private boolean checkSumFailed(HttpServletRequest request, String requestBody) {
        String curTime = request.getHeader("CurTime");
        String checksum = request.getHeader("CheckSum");
        String verifyMD5 = CheckSumBuilder.getMD5(requestBody);
        String verifyChecksum = CheckSumBuilder.getCheckSum(verifyMD5, curTime, yunXinConfigProperties.getAppSecret());
        log.info("verify checksum old checksum:{} new checksum:{}", checksum, verifyChecksum);
        return !verifyChecksum.equals(checksum);
    }
}
