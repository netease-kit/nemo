package com.netease.nemo.ktv;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.netease.nemo.Order;
import com.netease.nemo.OrderedRunner;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.context.Context;
import com.netease.nemo.entlive.dto.*;
import com.netease.nemo.entlive.enums.KtvActionEnum;
import com.netease.nemo.entlive.enums.LiveEnum;
import com.netease.nemo.entlive.enums.LiveTypeEnum;
import com.netease.nemo.entlive.parameter.*;
import com.netease.nemo.entlive.parameter.neroomNotify.JoinRoomEventNotify;
import com.netease.nemo.entlive.parameter.neroomNotify.RoomMember;
import com.netease.nemo.enums.NeRoomNotifyEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.openApi.YunXinServer;
import com.netease.nemo.openApi.dto.response.NeRoomResponse;
import com.netease.nemo.param.HostActionParam;
import com.netease.nemo.param.UserActionParam;
import com.netease.nemo.service.NotifyService;
import com.netease.nemo.util.UUIDUtil;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @Author：CH
 * @Date：2023/8/31 3:22 PM
 */
@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles(value = "local")
@RunWith(OrderedRunner.class)
public class KtvControllerTest {

    @Autowired
    private MockMvc mockMvc;

    public static TestContext testContext;

    @Resource
    private YunXinServer yunXinServer;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    private static boolean isInitRoom = false;

    @Autowired
    @Qualifier("neRoomNotifyServiceImpl")
    private NotifyService notifyService;

    @Before
    public void setUp() throws Exception {
        if (!isInitRoom) {
            testContext = new TestContext();
            // 创建直播
            initCreateLive();

            // 查询直播间信息
            LiveDto liveDto = testContext.getLiveIntroDto().getLive();

            // mock主播进入NeRoom
            hostJoinNeroom(liveDto);

            audienceJoinNeroom(liveDto);
            // 查询直播间信息
            getLiveInfo(liveDto.getId());
            isInitRoom = true;
        }
    }


    @Test
    @Order(10)
    public void closeLive() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/live/destroyLive";
        LiveParam param = new LiveParam();
        param.setLiveRecordId(testContext.getLiveIntroDto().getLive().getId());

        /* 请求头集合 */
        singRequest(hostHeader(), urlTemplate, GsonUtil.toJson(param));
    }

    private void hostOrderSong(Long liveRecordId, String songId) throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/live/song/orderSong";
        /* 请求头集合 */
        HttpHeaders headers = hostHeader();

        OrderSongParam orderSongParam = new OrderSongParam();
        orderSongParam.setLiveRecordId(liveRecordId);
        orderSongParam.setSongName(songId);
        orderSongParam.setSongId(songId);
        orderSongParam.setSongCover(songId);
        orderSongParam.setChannel(1);
        orderSongParam.setSinger(songId);

        // 请求
        RequestBuilder request = MockMvcRequestBuilders
                // post请求
                .post(urlTemplate)
                // 数据类型
                .contentType(MediaType.APPLICATION_JSON)
                // 请求头
                .headers(headers)
                .content(GsonUtil.toJson(orderSongParam));

        MvcResult mvcResult = mockMvc.perform(request)
                // 打印日志
                .andDo(print())
                // 获取返回值
                .andReturn();

        //从返回值获取响应的内容
        String contentAsString = mvcResult.getResponse().getContentAsString();
        log.info("contentAsString:{}", contentAsString);

        JsonObject jsonObject = GsonUtil.fromJson(contentAsString, JsonObject.class);
        // 获取list成功
        Assertions.assertEquals(jsonObject.get("code").getAsInt(), HttpStatus.OK.value());
        OrderSongResultDto orderSongResultDto = GsonUtil.fromJson(jsonObject.get("data"), OrderSongResultDto.class);
        Assertions.assertTrue(orderSongResultDto != null);
    }

    @Test
    @Order(0)
    public void OrderSong() throws Exception {
        hostOrderSong(testContext.getLiveIntroDto().getLive().getId(), UUIDUtil.getUUID());
    }

    /**
     * 1、请求房间演唱信息
     * 2、主播开始演唱
     *
     * @throws Exception
     */
    @Test
    @Order(1)
    public void hostStartSing() throws Exception {
        // 获取当前房间播放歌曲信息
        SingDetailInfoDto singDetailInfoDto = getSingDetailInfoDto();
        // 主播开始演唱
        SingParam singInfoParam = new SingParam();
        singInfoParam.setRoomUuid(testContext.getLiveIntroDto().getLive().getRoomUuid());
        startSing(singInfoParam);
    }

    @Test
    @Order(3)
    public void soleSingAction() throws Exception {
        // 获取当前房间播放歌曲信息
        SingDetailInfoDto singDetailInfoDto = getSingDetailInfoDto();

        SingActionParam actionParam = new SingActionParam();

        // 主播暂停演唱
        actionParam.setAction(KtvActionEnum.PAUSE.getAction());
        actionParam.setRoomUuid(singDetailInfoDto.getRoomUuid());
        ktvAction(actionParam);

        // 主播继续演唱
        actionParam.setAction(KtvActionEnum.PLAY.getAction());
        actionParam.setRoomUuid(singDetailInfoDto.getRoomUuid());
        ktvAction(actionParam);

        // 主播结束演唱
        actionParam.setAction(KtvActionEnum.END.getAction());
        actionParam.setRoomUuid(singDetailInfoDto.getRoomUuid());
        ktvAction(actionParam);
    }


    @Test
    @Order(4)
    public void chorusSing() throws Exception {
        // 点歌
        hostOrderSong(testContext.getLiveIntroDto().getLive().getId(), "2");

        // 查询当前房间播放歌曲信息
        SingDetailInfoDto singDetailInfoDto = getSingDetailInfoDto();

        // 合唱邀请
        ChorusInviteParam chorusInviteParam = new ChorusInviteParam();
        chorusInviteParam.setRoomUuid(singDetailInfoDto.getRoomUuid());
        chorusInviteParam.setOrderId(singDetailInfoDto.getOrderId());
        ChorusControlResultDto chorusControlResultDto = chorusInvite(chorusInviteParam);

        // 合唱取消
        CancelChorusParam cancelChorusParam = new CancelChorusParam();
        cancelChorusParam.setRoomUuid(singDetailInfoDto.getRoomUuid());
        cancelChorusParam.setOrderId(singDetailInfoDto.getOrderId());
        cancelChorusParam.setChorusId(chorusControlResultDto.getChorusId());
        cancelChorusInvite(cancelChorusParam);

        // 再次合唱邀请
        chorusInviteParam.setRoomUuid(singDetailInfoDto.getRoomUuid());
        chorusInviteParam.setOrderId(singDetailInfoDto.getOrderId());
        ChorusControlResultDto chorusControlAgain = chorusInvite(chorusInviteParam);

        // 合唱加入
        JoinChorusParam joinChorusParam = new JoinChorusParam();
        joinChorusParam.setRoomUuid(singDetailInfoDto.getRoomUuid());
        joinChorusParam.setOrderId(singDetailInfoDto.getOrderId());
        joinChorusParam.setChorusId(chorusControlAgain.getChorusId());
        joinChorus(joinChorusParam);

        // 合唱Ready
        FinishChorusReadyParam finishChorusReadyParam = new FinishChorusReadyParam();
        finishChorusReadyParam.setRoomUuid(singDetailInfoDto.getRoomUuid());
        finishChorusReadyParam.setChorusId(chorusControlAgain.getChorusId());
        readyChorus(finishChorusReadyParam);

        // 开始合唱
        SingParam singParam = new SingParam();
        singParam.setRoomUuid(singDetailInfoDto.getRoomUuid());
        singParam.setChorusId(chorusControlAgain.getChorusId());
        startSing(singParam);

        // 放弃合唱
        AbandonSingParam abandon = new AbandonSingParam();
        abandon.setRoomUuid(singDetailInfoDto.getRoomUuid());
        abandon.setOrderId(chorusControlAgain.getOrderId());
        abandonSing(abandon);
    }

    private void abandonSing(AbandonSingParam abandonSingParam) throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/ktv/sing/abandon";
        singRequest(hostHeader(), urlTemplate, GsonUtil.toJson(abandonSingParam));
    }

    private JsonObject singRequest(HttpHeaders headers, String urlTemplate, String param) throws Exception {
        // 请求
        RequestBuilder request = MockMvcRequestBuilders
                // post请求
                .post(urlTemplate)
                // 数据类型
                .contentType(MediaType.APPLICATION_JSON)
                // 请求头
                .headers(headers)
                .content(param);

        MvcResult mvcResult = mockMvc.perform(request)
                // 打印日志
                .andDo(print())
                // 获取返回值
                .andReturn();

        //从返回值获取响应的内容
        String contentAsString = mvcResult.getResponse().getContentAsString();
        log.info("contentAsString:{}", contentAsString);

        JsonObject jsonObject = GsonUtil.fromJson(contentAsString, JsonObject.class);
        Assertions.assertEquals(jsonObject.get("code").getAsInt(), HttpStatus.OK.value());
        return jsonObject;
    }

    private void cancelChorusInvite(CancelChorusParam cancelChorusParam) throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/ktv/sing/chorus/cancel";
        /* 请求头集合 */
        singRequest(hostHeader(), urlTemplate, GsonUtil.toJson(cancelChorusParam));
    }

    @Test
    @Order(5)
    public void get_LiveList() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/live/list";
        /* 请求头集合 */
        HttpHeaders headers = audienceHeader();

        LiveListQueryParam gameRoomParam = new LiveListQueryParam();
        gameRoomParam.setLiveType(LiveTypeEnum.GAME_ROOM.getType());
        gameRoomParam.setPageNum(1);
        gameRoomParam.setPageSize(10);

        // 请求
        RequestBuilder request = MockMvcRequestBuilders
                // post请求
                .post(urlTemplate)
                // 数据类型
                .contentType(MediaType.APPLICATION_JSON)
                // 请求头
                .headers(headers)
                .content(GsonUtil.toJson(gameRoomParam));

        MvcResult mvcResult = mockMvc.perform(request)
                // 打印日志
                .andDo(print())
                // 获取返回值
                .andReturn();

        //从返回值获取响应的内容
        String contentAsString = mvcResult.getResponse().getContentAsString();
        log.info("contentAsString:{}", contentAsString);

        JsonObject jsonObject = GsonUtil.fromJson(contentAsString, JsonObject.class);
        // 获取list成功
        Assertions.assertEquals(jsonObject.get("code").getAsInt(), HttpStatus.OK.value());
    }

    private ChorusControlResultDto chorusInvite(ChorusInviteParam chorusInviteParam) throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/ktv/sing/chorus/invite";
        /* 请求头集合 */
        JsonObject jsonObject = singRequest(hostHeader(), urlTemplate, GsonUtil.toJson(chorusInviteParam));

        ChorusControlResultDto chorusControlResultDto = GsonUtil.fromJson(jsonObject.get("data"), ChorusControlResultDto.class);
        return  chorusControlResultDto;
    }


    private ChorusControlResultDto joinChorus(JoinChorusParam joinChorusParam) throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/ktv/sing/chorus/join";
        /* 请求头集合 */
        JsonObject jsonObject = singRequest(audienceHeader(), urlTemplate, GsonUtil.toJson(joinChorusParam));

        ChorusControlResultDto chorusControlResultDto = GsonUtil.fromJson(jsonObject.get("data"), ChorusControlResultDto.class);
        return  chorusControlResultDto;
    }


    private ChorusControlResultDto readyChorus(FinishChorusReadyParam finishChorusReadyParam) throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/ktv/sing/chorus/ready";
        /* 请求头集合 */
        JsonObject jsonObject = singRequest(audienceHeader(), urlTemplate, GsonUtil.toJson(finishChorusReadyParam));

        ChorusControlResultDto chorusControlResultDto = GsonUtil.fromJson(jsonObject.get("data"), ChorusControlResultDto.class);
        return  chorusControlResultDto;
    }

    public void ktvAction(SingActionParam actionParam) throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/ktv/sing/action";
        /* 请求头集合 */
        singRequest(hostHeader(), urlTemplate, GsonUtil.toJson(actionParam));
    }

    public void startSing(SingParam singInfoParam) throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/ktv/sing/start";
        /* 请求头集合 */
        singRequest(hostHeader(), urlTemplate, GsonUtil.toJson(singInfoParam));
    }

    public SingDetailInfoDto getSingDetailInfoDto() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/ktv/sing/info";
        /* 请求头集合 */
        HttpHeaders headers = audienceHeader();

        SingInfoParam singInfoParam = new SingInfoParam();
        singInfoParam.setRoomUuid(testContext.getLiveIntroDto().getLive().getRoomUuid());

        // 请求
        RequestBuilder request = MockMvcRequestBuilders
                // post请求
                .post(urlTemplate)
                // 数据类型
                .contentType(MediaType.APPLICATION_JSON)
                // 请求头
                .headers(headers)
                .content(GsonUtil.toJson(singInfoParam));

        MvcResult mvcResult = mockMvc.perform(request)
                // 打印日志
                .andDo(print())
                // 获取返回值
                .andReturn();

        //从返回值获取响应的内容
        String contentAsString = mvcResult.getResponse().getContentAsString();
        log.info("contentAsString:{}", contentAsString);

        JsonObject jsonObject = GsonUtil.fromJson(contentAsString, JsonObject.class);
        // 获取list成功
        Assertions.assertEquals(jsonObject.get("code").getAsInt(), HttpStatus.OK.value());
        SingDetailInfoDto singDetailInfoDto = GsonUtil.fromJson(jsonObject.get("data"), SingDetailInfoDto.class);
        Assertions.assertTrue(singDetailInfoDto != null);
        return singDetailInfoDto;
    }

    private void hostJoinNeroom(LiveDto liveDto) {
        Map<String, String> user = new HashMap<>();
        user.put("role", "host");
        user.put("userName", "主播");
        joinNeRoom("2281b35c3a46a4c419406581f04f2b13", liveDto.getRoomUuid(), hostHeader(), user);

        HostActionParam hostAction = new HostActionParam();
        hostAction.setAction(2);
        hostAction.setToUserUuid(liveDto.getUserUuid());
        hostOnSeat("2281b35c3a46a4c419406581f04f2b13", liveDto.getRoomUuid(), hostAction);

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


    private void audienceJoinNeroom(LiveDto liveDto) {
        Map<String, String> user = new HashMap<>();
        user.put("role", "audience");
        user.put("userName", "audience");
        joinNeRoom("2281b35c3a46a4c419406581f04f2b13", liveDto.getRoomUuid(), audienceHeader(), user);

        UserActionParam userActionParam = new UserActionParam();
        userActionParam.setAction(1);
        audienceOnSeat("2281b35c3a46a4c419406581f04f2b13", liveDto.getRoomUuid(), audienceHeader(), userActionParam);

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


    public void joinNeRoom(String appKey, String roomUuid, HttpHeaders httpHeaders, Map<String, String> user) {
        log.info("start joinNeRoom. param:{}", GsonUtil.toJson(user));
        if (user == null) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        String url = String.format("scene/apps/%s/v1/rooms/%s/entry", appKey, roomUuid);

        try {
            HttpEntity entity = new HttpEntity(user, httpHeaders);
            NeRoomResponse neRoomResponse = yunXinServer.requestForEntity(yunXinConfigProperties.getNeRoomHost(), url, HttpMethod.POST, entity, NeRoomResponse.class);
            Integer code = neRoomResponse.getCode();
            if (code == null || code != 0) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, neRoomResponse.getMsg());
            }
            if (neRoomResponse.getData() == null) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        } catch (BsException e) {
            throw e;
        } catch (Exception e) {
            log.info("joinNeRoom error.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void hostOnSeat(String appKey, String roomUuid, HostActionParam hostActionParam) {
        log.info("start hostOnSeat. param:{}", GsonUtil.toJson(hostActionParam));
        if (hostActionParam == null) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        String url = String.format("/scene/apps/%s/v1/rooms/%s/seat/host/action", appKey, roomUuid);

        try {
            HttpHeaders httpHeaders = hostHeader();
            HttpEntity entity = new HttpEntity(hostActionParam, httpHeaders);
            NeRoomResponse neRoomResponse = yunXinServer.requestForEntity(yunXinConfigProperties.getNeRoomHost(), url, HttpMethod.POST, entity, NeRoomResponse.class);
            Integer code = neRoomResponse.getCode();
            if (code == null || code != 0) {
                if (!"User Is On Seat.".equals(neRoomResponse.getMsg())) {
                    throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, neRoomResponse.getMsg());
                }
            }
        } catch (BsException e) {
            throw e;
        } catch (Exception e) {
            log.info("joinNeRoom error.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void audienceOnSeat(String appKey, String roomUuid, HttpHeaders httpHeaders, UserActionParam userActionParam) {
        log.info("start audienceOnSeat. param:{}", GsonUtil.toJson(userActionParam));
        if (userActionParam == null) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        String url = String.format("/scene/apps/%s/v1/rooms/%s/seat/user/action", appKey, roomUuid);

        try {
            HttpEntity entity = new HttpEntity(userActionParam, httpHeaders);
            NeRoomResponse neRoomResponse = yunXinServer.requestForEntity(yunXinConfigProperties.getNeRoomHost(), url, HttpMethod.POST, entity, NeRoomResponse.class);
            Integer code = neRoomResponse.getCode();
            if (code == null || code != 0) {
                if (!"User Is On Seat.".equals(neRoomResponse.getMsg())) {
                    throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, neRoomResponse.getMsg());
                }
            }
        } catch (BsException e) {
            throw e;
        } catch (Exception e) {
            log.info("joinNeRoom error.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private CreateLiveParam initCreateLiveParam() {
        CreateLiveParam param = new CreateLiveParam();
        param.setSeatCount(8);
        param.setCover("Test.jpg");
        param.setLiveTopic("KTV");
        param.setLiveType(LiveTypeEnum.KTV.getType());
        param.setSeatMode(0);
        param.setSeatApplyMode(0);
        param.setSeatInviteMode(0);
        return param;
    }

    public void initCreateLive() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/live/createLive";
        CreateLiveParam createLiveParam = initCreateLiveParam();

        /* 请求头集合 */
        JsonObject jsonObject = singRequest(hostHeader(), urlTemplate, GsonUtil.toJson(createLiveParam));


        LiveIntroDto liveIntroDto = GsonUtil.fromJson(jsonObject.get("data"), LiveIntroDto.class);
        testContext.setLiveIntroDto(liveIntroDto);
    }

    /**
     * 查询直播间信息
     *
     * @param liveRecordId liveRecordId
     * @throws Exception
     */
    public void getLiveInfo(Long liveRecordId) throws Exception {
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


    private HttpHeaders hostHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", "BEHI6ZAWDX83E9SA07QG0NNG");
        headers.add("user", "ch");
        headers.add("appkey", "2281b35c3a46a4c419406581f04f2b13");
        headers.add("clientType", "web");
        headers.add("deviceId", "267437774082551");
        headers.add("authType", "3");
        return headers;
    }

    private HttpHeaders audienceHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", "6NQGDW4O0F1RPK30VC69QWRK");
        headers.add("user", "ch1");
        headers.add("appkey", "2281b35c3a46a4c419406581f04f2b13");
        headers.add("clientType", "web");
        headers.add("deviceId", "267437774082561");
        headers.add("authType", "3");
        return headers;
    }
}
