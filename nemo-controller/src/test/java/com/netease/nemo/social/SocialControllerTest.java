package com.netease.nemo.social;

import com.google.gson.JsonObject;
import com.netease.nemo.BaseTest;
import com.netease.nemo.Order;
import com.netease.nemo.OrderedRunner;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.service.UserService;
import com.netease.nemo.socialchat.parameter.GetOnLineUserListParam;
import com.netease.nemo.socialchat.parameter.GetUserStateParam;
import com.netease.nemo.socialchat.parameter.UserRewardParam;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
public class SocialControllerTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void unSetUp() throws Exception {
    }

    @Test
    @Order(0)
    public void test_reporter() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/socialChat/user/reporter";

        /* 请求头集合 */
        HttpHeaders headers = hostHeader();

        // 请求
        RequestBuilder request = MockMvcRequestBuilders
                // post请求
                .post(urlTemplate)
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

        // 开播成功
        Assertions.assertEquals(jsonObject.get("code").getAsInt(), HttpStatus.OK.value());
    }

    @Test
    @Order(1)
    public void test_getOnlineUser() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/socialChat/user/getOnlineUser";
        GetOnLineUserListParam param = new GetOnLineUserListParam();
        param.setPageNum(1);
        param.setPageSize(10);

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

    @Test
    @Order(2)
    public void test_getUserState() throws Exception {
        UserDto userDto = userService.getUser("ch");
        // 请求地址
        String urlTemplate = "/nemo/socialChat/user/getUserState";
        GetUserStateParam param = new GetUserStateParam();
        param.setMobile(userDto.getMobile());

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

    @Test
    @Order(2)
    public void test_reward() throws Exception {
        // 请求地址
        String urlTemplate = "/nemo/socialChat/user/reward";
        UserRewardParam param = new UserRewardParam();
        param.setGiftId(1L);
        param.setGiftCount(10);
        param.setTarget("ch");

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
