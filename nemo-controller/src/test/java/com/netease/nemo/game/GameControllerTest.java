package com.netease.nemo.game;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.netease.nemo.Order;
import com.netease.nemo.OrderedRunner;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.context.Context;
import com.netease.nemo.entlive.dto.LiveDto;
import com.netease.nemo.entlive.dto.LiveIntroDto;
import com.netease.nemo.entlive.enums.LiveEnum;
import com.netease.nemo.entlive.enums.LiveTypeEnum;
import com.netease.nemo.entlive.parameter.CreateLiveParam;
import com.netease.nemo.entlive.parameter.LiveListQueryParam;
import com.netease.nemo.entlive.parameter.LiveParam;
import com.netease.nemo.entlive.parameter.neroomNotify.JoinRoomEventNotify;
import com.netease.nemo.entlive.parameter.neroomNotify.RoomMember;
import com.netease.nemo.enums.NeRoomNotifyEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.game.dto.GameInfoDto;
import com.netease.nemo.game.dto.GameRoomInfoDto;
import com.netease.nemo.game.dto.GameRoomMemberDto;
import com.netease.nemo.game.request.GameRoomParam;
import com.netease.nemo.openApi.YunXinServer;
import com.netease.nemo.openApi.dto.response.NeRoomResponse;
import com.netease.nemo.param.HostActionParam;
import com.netease.nemo.param.UserActionParam;
import com.netease.nemo.service.NotifyService;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
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
public class GameControllerTest {

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

            // 设置游戏列表到上下文
            getGameList();
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

    public void getGameList() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/game/list";
        /* 请求头集合 */
        HttpHeaders headers = hostHeader();

        // 请求
        RequestBuilder request = MockMvcRequestBuilders
                // post请求
                .get(urlTemplate)
                // 数据类型
                .contentType(MediaType.APPLICATION_JSON)
                // 请求头
                .headers(headers);

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
        List<GameInfoDto> gameInfoDtos = GsonUtil.fromJson(jsonObject.get("data"), new TypeToken<List<GameInfoDto>>() {
        }.getType());
        Assertions.assertTrue(gameInfoDtos.size() > 0);

        testContext.setGameInfoDtoList(gameInfoDtos);
    }

    @Test
    @Order(1)
    public void createGameRoom() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/game/create";
        /* 请求头集合 */
        HttpHeaders headers = hostHeader();

        GameRoomParam gameRoomParam = new GameRoomParam();
        gameRoomParam.setGameId(testContext.getGameInfoDtoList().get(0).getGameId());
        gameRoomParam.setRoomUuid(testContext.getLiveIntroDto().getLive().getRoomUuid());

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
        GameRoomInfoDto gameRoomInfoDto = GsonUtil.fromJson(jsonObject.get("data"), GameRoomInfoDto.class);
        Assertions.assertTrue(gameRoomInfoDto != null);
        testContext.setGameRoomInfoDto(gameRoomInfoDto);
    }

    @Test
    @Order(2)
    public void joinGameRoom() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/game/join";
        /* 请求头集合 */
        HttpHeaders headers = hostHeader();

        GameRoomParam gameRoomParam = new GameRoomParam();
        gameRoomParam.setGameId(testContext.getGameInfoDtoList().get(0).getGameId());
        gameRoomParam.setRoomUuid(testContext.getLiveIntroDto().getLive().getRoomUuid());

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
        GameRoomMemberDto gameRoomMemberDto = GsonUtil.fromJson(jsonObject.get("data"), GameRoomMemberDto.class);
        Assertions.assertTrue(gameRoomMemberDto != null);
    }

    @Test
    @Order(2)
    public void audienceJoinGameRoom() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/game/join";
        /* 请求头集合 */
        HttpHeaders headers = audienceHeader();

        GameRoomParam gameRoomParam = new GameRoomParam();
        gameRoomParam.setGameId(testContext.getGameInfoDtoList().get(0).getGameId());
        gameRoomParam.setRoomUuid(testContext.getLiveIntroDto().getLive().getRoomUuid());

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
        GameRoomMemberDto gameRoomMemberDto = GsonUtil.fromJson(jsonObject.get("data"), GameRoomMemberDto.class);
        Assertions.assertTrue(gameRoomMemberDto != null);
    }


    @Test
    @Order(3)
    public void getGameMember() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/game/members";
        /* 请求头集合 */
        HttpHeaders headers = hostHeader();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("gameId", testContext.getGameInfoDtoList().get(0).getGameId());
        params.add("roomUuid", testContext.getLiveIntroDto().getLive().getRoomUuid());

        // 请求
        RequestBuilder request = MockMvcRequestBuilders
                // post请求
                .get(urlTemplate)
                // 数据类型
                .contentType(MediaType.APPLICATION_JSON)
                // 请求头
                .headers(headers)
                .params(params);

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
        List<GameRoomMemberDto> gameRoomMemberDtos = GsonUtil.fromJson(jsonObject.get("data"), new TypeToken<List<GameRoomMemberDto>>() {
        }.getType());
        Assertions.assertTrue(gameRoomMemberDtos != null && gameRoomMemberDtos.size() > 0);
    }

    @Test
    @Order(4)
    public void startGameRoom() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/game/start";
        /* 请求头集合 */
        HttpHeaders headers = hostHeader();

        GameRoomParam gameRoomParam = new GameRoomParam();
        gameRoomParam.setGameId(testContext.getGameInfoDtoList().get(0).getGameId());
        gameRoomParam.setRoomUuid(testContext.getLiveIntroDto().getLive().getRoomUuid());

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

    @Test
    @Order(4)
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


    @Test
    @Order(5)
    public void audienceExitGameRoom() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/game/exit";
        /* 请求头集合 */
        HttpHeaders headers = audienceHeader();

        GameRoomParam gameRoomParam = new GameRoomParam();
        gameRoomParam.setGameId(testContext.getGameInfoDtoList().get(0).getGameId());
        gameRoomParam.setRoomUuid(testContext.getLiveIntroDto().getLive().getRoomUuid());

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
        Assertions.assertEquals(jsonObject.get("code").getAsInt(), HttpStatus.OK.value());
    }

    @Test
    @Order(6)
    public void hostEndGameRoom() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/game/end";
        /* 请求头集合 */
        HttpHeaders headers = hostHeader();

        GameRoomParam gameRoomParam = new GameRoomParam();
        gameRoomParam.setGameId(testContext.getGameInfoDtoList().get(0).getGameId());
        gameRoomParam.setRoomUuid(testContext.getLiveIntroDto().getLive().getRoomUuid());

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
                if(!"User Is On Seat.".equals(neRoomResponse.getMsg())) {
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

    public void audienceOnSeat(String appKey, String roomUuid,   HttpHeaders httpHeaders, UserActionParam userActionParam) {
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
                if(!"User Is On Seat.".equals(neRoomResponse.getMsg())) {
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
        param.setLiveTopic("TestGame");
        param.setLiveType(LiveTypeEnum.GAME_ROOM.getType());
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
