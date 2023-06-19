package com.netease.nemo.controller.entertainmentLive;

import com.google.gson.JsonObject;
import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.service.NotifyService;
import com.netease.nemo.util.CheckSumBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
            String verifyChecksum = CheckSumBuilder.getCheckSum(verifyMD5, curTime, yunXinConfigProperties.getAppSecret());
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
}
