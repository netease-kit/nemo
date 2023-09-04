package com.netease.nemo;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.netease.nemo.context.Context;
import com.netease.nemo.entlive.dto.LiveDto;
import com.netease.nemo.entlive.dto.LiveIntroDto;
import com.netease.nemo.entlive.enums.LiveEnum;
import com.netease.nemo.entlive.enums.LiveTypeEnum;
import com.netease.nemo.entlive.parameter.CreateLiveParam;
import com.netease.nemo.entlive.parameter.LiveListQueryParam;
import com.netease.nemo.entlive.parameter.LiveParam;
import com.netease.nemo.entlive.parameter.LiveRewardParam;
import com.netease.nemo.entlive.parameter.neroomNotify.JoinRoomEventNotify;
import com.netease.nemo.entlive.parameter.neroomNotify.RoomMember;
import com.netease.nemo.enums.NeRoomNotifyEnum;
import com.netease.nemo.service.NotifyService;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @Author：CH
 * @Date：2023/9/4 3:10 PM
 */
@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles(value = "local")
public class BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    public TestContext testContext;

    @Autowired
    @Qualifier("neRoomNotifyServiceImpl")
    protected NotifyService notifyService;

    protected void closeLive() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/live/destroyLive";
        LiveParam param = new LiveParam();
        param.setLiveRecordId(testContext.getLiveIntroDto().getLive().getId());

        /* 请求头集合 */
        HttpHeaders headers = hostHeader();

        // 请求
        RequestBuilder request = MockMvcRequestBuilders
                // post请求
                .post(urlTemplate)
                // 数据类型
                .contentType(MediaType.APPLICATION_JSON)
                // 请求头
                .headers(headers)
                // 请求体
                .content(GsonUtil.toJson(param));

        MvcResult mvcResult = mockMvc.perform(request)
                // 打印日志
                .andDo(print())
                // 获取返回值
                .andReturn();

        //从返回值获取响应的内容
        String contentAsString = mvcResult.getResponse().getContentAsString();
        log.info("contentAsString:{}", contentAsString);

        JsonObject jsonObject = GsonUtil.fromJson(contentAsString, JsonObject.class);

        // 开播成功
        Assertions.assertEquals(jsonObject.get("code").getAsInt(), HttpStatus.OK.value());
    }

    protected CreateLiveParam initCreateLiveParam() {
        CreateLiveParam param = new CreateLiveParam();
        param.setSeatCount(2);
        param.setCover("Test.jpg");
        param.setLiveTopic("TestGame");
        param.setLiveType(LiveTypeEnum.LISTEN_TOGETHER.getType());
        return param;
    }

    protected void initCreateLive() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/live/createLive";
        CreateLiveParam createLiveParam = initCreateLiveParam();

        /* 请求头集合 */
        HttpHeaders headers = hostHeader();

        // 请求
        RequestBuilder request = MockMvcRequestBuilders
                // post请求
                .post(urlTemplate)
                // 数据类型
                .contentType(MediaType.APPLICATION_JSON)
                // 请求头
                .headers(headers)
                // 请求体
                .content(GsonUtil.toJson(createLiveParam));

        MvcResult mvcResult = mockMvc.perform(request)
                // 打印日志
                .andDo(print())
                // 获取返回值
                .andReturn();

        //从返回值获取响应的内容
        String contentAsString = mvcResult.getResponse().getContentAsString();
        log.info("contentAsString:{}", contentAsString);

        JsonObject jsonObject = GsonUtil.fromJson(contentAsString, JsonObject.class);

        // 开播成功
        Assertions.assertEquals(jsonObject.get("code").getAsInt(), HttpStatus.OK.value());

        LiveIntroDto liveIntroDto = GsonUtil.fromJson(jsonObject.get("data"), LiveIntroDto.class);
        testContext.setLiveIntroDto(liveIntroDto);
    }

    /**
     * 查询直播间信息
     *
     * @param liveRecordId liveRecordId
     * @throws Exception
     */
    protected void getLiveInfo(Long liveRecordId) throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/live/info";
        /* 请求头集合 */
        HttpHeaders headers = hostHeader();

        LiveParam liveParam = new LiveParam();
        liveParam.setLiveRecordId(liveRecordId);

        // 请求
        RequestBuilder request = MockMvcRequestBuilders
                // post请求
                .post(urlTemplate)
                // 数据类型
                .contentType(MediaType.APPLICATION_JSON)
                // 请求头
                .headers(headers)
                .content(GsonUtil.toJson(liveParam));


        MvcResult mvcResult = mockMvc.perform(request)
                // 打印日志
                .andDo(print())
                // 获取返回值
                .andReturn();

        //从返回值获取响应的内容
        String contentAsString = mvcResult.getResponse().getContentAsString();
        log.info("contentAsString:{}", contentAsString);

        JsonObject jsonObject = GsonUtil.fromJson(contentAsString, JsonObject.class);

        // 直播信息查询成功
        Assertions.assertEquals(jsonObject.get("code").getAsInt(), HttpStatus.OK.value());

        LiveIntroDto liveIntroDto = GsonUtil.fromJson(jsonObject.get("data"), LiveIntroDto.class);
        Assertions.assertEquals(LiveEnum.LIVE.getCode(), liveIntroDto.getLive().getLive().intValue());

        // 设置最新的直播信息到上下文
        testContext.setLiveIntroDto(liveIntroDto);
    }

    protected void hostJoinNeroom(LiveDto liveDto) {
        JsonObject body = new JsonObject();
        body.addProperty("requestId", "7b6319d1cb5e430e94642bd5ecd0b8ff");
        body.addProperty("eventType", NeRoomNotifyEnum.USER_JOIN_ROOM.getEventType());
        body.addProperty("timestamp", System.currentTimeMillis());
        JoinRoomEventNotify joinRoomEventNotify = JoinRoomEventNotify.builder()
                .roomArchiveId(liveDto.getRoomArchiveId())
                .roomUuid(liveDto.getRoomUuid())
                .users(Sets.newHashSet(RoomMember.builder().userUuid(liveDto.getUserUuid()).role("host").build()))
                .build();
        body.add("body", GsonUtil.toJsonTree(joinRoomEventNotify));
        Context.init("7b6319d1cb5e430e94642bd5ecd0b8ff");
        Context.get().setAppKey("2281b35c3a46a4c419406581f04f2b13");
        notifyService.handlerNotify(GsonUtil.toJson(body));
        Context.get().unload();
    }

    protected void audienceJoinNeroom(LiveDto liveDto) {
        JsonObject body = new JsonObject();
        body.addProperty("requestId", "7b6319d1cb5e430e94642bd5ecd0b8ff");
        body.addProperty("eventType", NeRoomNotifyEnum.USER_JOIN_ROOM.getEventType());
        body.addProperty("timestamp", System.currentTimeMillis());
        JoinRoomEventNotify joinRoomEventNotify = JoinRoomEventNotify.builder()
                .roomArchiveId(liveDto.getRoomArchiveId())
                .roomUuid(liveDto.getRoomUuid())
                .users(Sets.newHashSet(RoomMember.builder().userUuid("ch1").role("audience").build()))
                .build();
        body.add("body", GsonUtil.toJsonTree(joinRoomEventNotify));
        Context.init("7b6319d1cb5e430e94642bd5ecd0b8ff");
        Context.get().setAppKey("2281b35c3a46a4c419406581f04f2b13");
        notifyService.handlerNotify(GsonUtil.toJson(body));
        Context.get().unload();
    }


    protected HttpHeaders hostHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", "BEHI6ZAWDX83E9SA07QG0NNG");
        headers.add("user", "ch");
        headers.add("appkey", "2281b35c3a46a4c419406581f04f2b13");
        return headers;
    }

    protected HttpHeaders audienceHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", "6NQGDW4O0F1RPK30VC69QWRK");
        headers.add("user", "ch1");
        headers.add("appkey", "2281b35c3a46a4c419406581f04f2b13");
        return headers;
    }
}
