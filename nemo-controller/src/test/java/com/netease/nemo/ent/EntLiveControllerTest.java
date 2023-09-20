package com.netease.nemo.ent;

import com.google.gson.JsonObject;
import com.netease.nemo.BaseTest;
import com.netease.nemo.Order;
import com.netease.nemo.OrderedRunner;
import com.netease.nemo.TestContext;
import com.netease.nemo.entlive.dto.LiveDto;
import com.netease.nemo.entlive.enums.LiveTypeEnum;
import com.netease.nemo.entlive.parameter.LiveListQueryParam;
import com.netease.nemo.entlive.parameter.LiveRewardParam;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @Author：CH
 * @Date：2023/8/31 3:33 PM
 */
@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles(value = "local")
@RunWith(OrderedRunner.class)
public class EntLiveControllerTest extends BaseTest {

    @Before
    public void setUp() throws Exception {
        testContext = new TestContext();
        // 创建直播
        initCreateLive(LiveTypeEnum.CHAT.getType());

        // 查询直播间信息
        LiveDto liveDto = testContext.getLiveIntroDto().getLive();

        // mock主播进入NeRoom
        hostJoinNeroom(liveDto);

        // 查询直播间信息
        getLiveInfo(liveDto.getId());
    }

    @After
    public void unSetUp() throws Exception {
        closeLive();
    }

    @Test
    @Order(0)
    public void test_GetListLive() throws Exception {
        // 请求地址
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/live/list";
        LiveListQueryParam param = new LiveListQueryParam();
        param.setLiveType(LiveTypeEnum.CHAT.getType());
        param.setPageNum(1);
        param.setPageSize(10);

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

    @Test
    @Order(1)
    public void test_Reward() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/entertainmentLive/live/batch/reward";
        LiveRewardParam param = new LiveRewardParam();
        param.setLiveRecordId(testContext.getLiveIntroDto().getLive().getId());
        param.setTargets(Arrays.asList("ch"));
        param.setGiftId(1L);
        param.setGiftCount(10);

        /* 请求头集合 */
        HttpHeaders headers = audienceHeader();

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

        // 打赏成功
        Assertions.assertEquals(jsonObject.get("code").getAsInt(), HttpStatus.OK.value());
    }
}
