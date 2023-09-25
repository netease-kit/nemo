package com.netease.nemo.openApi;

import com.google.gson.JsonObject;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.openApi.dto.nim.ImMsgDto;
import com.netease.nemo.openApi.dto.nim.YunxinCreateLiveChannelDto;
import com.netease.nemo.openApi.dto.response.ImResponse;
import com.netease.nemo.openApi.dto.response.LiveWallSolutionResponse;
import com.netease.nemo.openApi.enums.ImMsgTypeEnum;
import com.netease.nemo.openApi.paramters.StopLiveWallSolutionTaskParam;
import com.netease.nemo.openApi.paramters.SubmitLiveWallSolutionTaskParam;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 云信IM、rtc相关API
 */
@Service
@Slf4j
public class NimService {

    @Resource
    private YunXinServer yunXinServer;

    @Value(value = "business.systemAccid")
    private String systemAccid;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;


    /**
     * 创建 IM 用户
     *
     * @param accid IM 账号
     * @param name  IM 账号名字
     * @param token IM 令牌
     */
    public boolean createUser(String accid, String name, String icon, String token) {
        log.info("start createUser. accid:{},name:{},icon:{},token:{}", accid, name, icon, token);
        String url = "/nimserver/user/create.action";
        Map<String, String> params = new HashMap<>();
        params.put("accid", accid);
        params.put("name", name);
        params.put("icon", icon);
        params.put("token", token);

        ImResponse result = yunXinServer.requestForPostEntityNim(url, HttpMethod.POST, params, null, ImResponse.class);
        if (result == null) {
            log.error("createUser error.");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        Integer code = result.getCode();
        if (code == null || code != 200) {
            String desc = result.getDesc();
            if (StringUtils.equalsIgnoreCase(desc, "already register")) {
                log.error("imAccid already register");
                return false;
            }
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return true;
    }


    /**
     * 添加好友
     *
     * @param accId  IM 账号
     * @param fAccId 好友IM账号
     * @param type   1直接加好友，2请求加好友，3同意加好友，4拒绝加好友
     * @param msg    加好友对应的请求消息，第三方组装，最长256字符
     */
    public void addFriend(String accId, String fAccId, Integer type, String msg) {
        log.info("start addFriend. accId:{},fAccId:{},type:{},msg:{}", accId, fAccId, type, msg);
        String url = "/nimserver/friend/add.action";
        Map<String, String> params = new HashMap<>();
        params.put("accid", accId);
        params.put("faccid", fAccId);
        params.put("type", String.valueOf(type));
        params.put("msg", msg);

        ImResponse result = yunXinServer.requestForPostEntityNim(url, HttpMethod.POST, params, null, ImResponse.class);
        if (result == null) {
            log.error("addFriend error.");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        Integer code = result.getCode();
        if (code == null || code != 200) {
            log.info("添加好友失败。");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 发送IM自定义消息
     *
     * @param from 发送者的云信 IM 账号
     * @param to   消息的接收方
     * @param ope  0：单聊消息，1：群消息（高级群）
     * @param body msg
     */
    public void sendImCustomMsg(String from, String to, int ope, Object body) {
        sendImMsg(from, to, ope, ImMsgTypeEnum.CUSTOM_MESSAGE.getType(), body);
    }

    /**
     * 发送IM消息
     *
     * @param from 发送者的云信 IM 账号
     * @param to   消息的接收方
     * @param ope  0：单聊消息，1：群消息（高级群）
     * @param body msg
     */
    public void sendImMsg(String from, String to, int ope, int type, Object body) {
        log.info("start sendImMsg. from:{},to:{},ope:{},type:{},body:{}", from, to, ope, type, GsonUtil.toJson(body));
        String url = "/nimserver/msg/sendMsg.action";
        Map<String, String> params = new HashMap<>();
        params.put("from", from);
        params.put("ope", String.valueOf(ope));
        params.put("to", to);
        params.put("type", String.valueOf(type));

        if (ImMsgTypeEnum.TEXT_MESSAGE.getType() == type) {
            params.put("body", GsonUtil.toJson(new ImMsgDto(body)));
        } else {
            params.put("body", GsonUtil.toJson(body));
        }

        ImResponse result = yunXinServer.requestForPostEntityNim(url, HttpMethod.POST, params, null, ImResponse.class);
        if (result == null) {
            log.error("sendImMsg error.");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        Integer code = result.getCode();
        if (code == null || code != 200) {
            log.info("发送消息失败。");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 发送自定义系统通知
     *
     * @param from 发送者的云信 IM 账号
     * @param to   消息的接收方
     * @param msgtype  0：点对点自定义通知，1：群消息自定义通知
     * @param attach attach 自定义系统通知的具体内容，开发者组装的字符串，建议 JSON 格式，最大长度 4096 字符
     */
    public void sendAttachMsg(String from, String to, int msgtype, Object attach) {
        log.info("start sendAttachMsg. from:{}, to:{}, msgtype:{}, attach:{}", from, to, msgtype, GsonUtil.toJson(attach));

        String url = "/nimserver/msg/sendAttachMsg.action";
        Map<String, String> params = new HashMap<>();
        params.put("from", from);
        params.put("to", to);
        params.put("msgtype", String.valueOf(msgtype));
        params.put("attach", GsonUtil.toJson(attach));

        ImResponse result = yunXinServer.requestForPostEntityNim(url, HttpMethod.POST, params, null, ImResponse.class);
        if (result == null) {
            log.error("sendAttachMsg error.");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        Integer code = result.getCode();
        if (code == null || code != 200) {
            log.info("发送自定义系统通知。");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 批量发送自定义系统通知
     *
     * @param fromAccid 发送者的云信 IM 账号
     * @param toAccids   接收者列表， ["aaa","bbb"]（JSONArray 对应的 accid，如果解析出错，会报 414 错误），最多 5000 人
     * @param attach attach 自定义系统通知的具体内容，开发者组装的字符串，建议 JSON 格式，最大长度 4096 字符
     */
    public void sendBatchAttachMsg(String fromAccid, List<String> toAccids, Object attach) {
        log.info("start sendBatchAttachMsg. fromAccid:{},toAccids:{},attach:{}", fromAccid, toAccids, GsonUtil.toJson(attach));

        String url = "/nimserver/msg/sendBatchAttachMsg.action";
        Map<String, String> params = new HashMap<>();
        params.put("fromAccid", fromAccid);
        params.put("toAccids", GsonUtil.toJson(toAccids));
        params.put("attach", GsonUtil.toJson(attach));

        ImResponse result = yunXinServer.requestForPostEntityNim(url, HttpMethod.POST, params, null, ImResponse.class);
        if (result == null) {
            log.error("sendBatchAttachMsg error.");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        Integer code = result.getCode();
        if (code == null || code != 200) {
            log.info("批量发送自定义系统通知失败。");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 创建安全通审核任务
     */
    public void submitSolution(SubmitLiveWallSolutionTaskParam submitLiveWallSolutionTaskParam) {
        log.info("start submitSolution. param:{}", GsonUtil.toJson(submitLiveWallSolutionTaskParam));
        String url = "/livewallsolution/submit";
        if (submitLiveWallSolutionTaskParam == null
                || StringUtils.isEmpty(submitLiveWallSolutionTaskParam.getChannelName())
                || submitLiveWallSolutionTaskParam.getMonitorUid() == null) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        try {
            LiveWallSolutionResponse liveWallSolutionResponse = yunXinServer.requestPostEntityForSecurity(url, submitLiveWallSolutionTaskParam);
            if (liveWallSolutionResponse.getResult() == null) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            Integer code = liveWallSolutionResponse.getCode();
            if (code == null || code != 200) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, liveWallSolutionResponse.getMsg());
            }
        } catch (BsException e) {
            throw e;
        } catch (Exception e) {
            log.info("submitSolution error.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 停止安全通任务
     */
    public void feedbackSolution(String channelName) {
        log.info("start feedbackSolution. channelName:{}", channelName);
        String url = "/livewallsolution/feedback";
        if (StringUtils.isEmpty(channelName)) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        try {
            StopLiveWallSolutionTaskParam stopSolutionTaskParam = new StopLiveWallSolutionTaskParam();
            StopLiveWallSolutionTaskParam.ChannelInfo rtcInfo = new StopLiveWallSolutionTaskParam.ChannelInfo(channelName, 100);
            List<StopLiveWallSolutionTaskParam.ChannelInfo> realTimeInfoList = new ArrayList<>();
            realTimeInfoList.add(rtcInfo);
            stopSolutionTaskParam.setRealTimeInfoList(realTimeInfoList);

            LiveWallSolutionResponse liveWallSolutionResponse = yunXinServer.requestPostEntityForSecurity(url, stopSolutionTaskParam);
            if (liveWallSolutionResponse == null) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            Integer code = liveWallSolutionResponse.getCode();
            if (code == null || code != 200) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, liveWallSolutionResponse.getMsg());
            }
        } catch (BsException e) {
            throw e;
        } catch (Exception e) {
            log.info("feedbackSolution error.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除RTC房间
     */
    public void deleteRtcRoom(Long cid) {
        log.info("start deleteRtcRoom. cid:{}", cid);
        if (cid == null) {
            log.error("deleteRtcRoom params empty,cid:{}", cid);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "deleteRtcRoom params empty");
        }
        String url = "/v2/api/rooms/" + cid;
        HttpHeaders httpHeaders = new HttpHeaders();
        yunXinServer.addCheckSumHeader(httpHeaders);
        httpHeaders.add("Content-Type", "application/json;charset=utf-8");
        HttpEntity entity = new HttpEntity(null, httpHeaders);

        try {
            String res = yunXinServer.requestForEntity(yunXinConfigProperties.getRtcHost(), url, HttpMethod.DELETE, entity, String.class);
            log.info("deleteRtcRoom delete url:{},res:{}", url, res);
        } catch (Exception e) {
            log.info("deleteRtcRoom error.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public YunxinCreateLiveChannelDto createLive(String name, Integer type) {
        log.info("createLive start");
        JsonObject jo = new JsonObject();
        jo.addProperty("name", name);
        if(null != type) {
            jo.addProperty("type", type);
        }

        String url = "/app/channel/create";

        HttpHeaders httpHeaders = new HttpHeaders();
        yunXinServer.addCheckSumHeader(httpHeaders);
        httpHeaders.add("Content-Type", "application/json; charset=utf-8");
        HttpEntity entity = new HttpEntity(GsonUtil.toJson(jo), httpHeaders);

        String response = yunXinServer.requestForEntity(yunXinConfigProperties.getNeLiveHost(), url, HttpMethod.POST, entity, String.class);
        if (StringUtils.isEmpty(response)) {
            throw new BsException(ErrorCode.FORBIDDEN);
        }

        JsonObject bodyJo = GsonUtil.parseJsonObjectIfNotNull(response);
        Integer code = GsonUtil.getInt(bodyJo, "code");
        if (code == null || code != 200) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        JsonObject retJo = GsonUtil.getJsonObject(bodyJo, "ret");
        if (retJo == null) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        String pushUrl = GsonUtil.getString(retJo, "pushUrl");
        String rtmpPullUrl = GsonUtil.getString(retJo, "rtmpPullUrl");
        String hlsPullUrl = GsonUtil.getString(retJo, "hlsPullUrl");
        String httpPullUrl = GsonUtil.getString(retJo, "httpPullUrl");

        if (StringUtils.isAnyEmpty(pushUrl, rtmpPullUrl, hlsPullUrl, httpPullUrl)) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        log.info("createLive end");
        return GsonUtil.fromJson(retJo, YunxinCreateLiveChannelDto.class);
    }

    public void deleteLiveChannel( String cid) {
        log.info("deleteLiveChannel start");
        if (StringUtils.isEmpty(cid)) {
            log.warn("deleteLiveChannel cid is empty");
            return;
        }
        JsonObject jo = new JsonObject();
        jo.addProperty("cid", cid);
        String url = "/app/channel/delete";
        HttpHeaders httpHeaders = new HttpHeaders();
        yunXinServer.addCheckSumHeader(httpHeaders);
        httpHeaders.add("Content-Type", "application/json; charset=utf-8");
        HttpEntity entity = new HttpEntity(jo.toString(), httpHeaders);

        String response = yunXinServer.requestForEntity(yunXinConfigProperties.getNeLiveHost(), url, HttpMethod.POST, entity, String.class);
        if (StringUtils.isEmpty(response)) {
            throw new BsException(ErrorCode.FORBIDDEN);
        }
        JsonObject bodyJo = GsonUtil.parseJsonObjectIfNotNull(response);
        Integer code = GsonUtil.getInt(bodyJo, "code");
        if (code == null || code != 200) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        log.info("deleteLiveChannel end");
    }

}
