package com.netease.nemo;

import com.google.gson.JsonObject;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.entlive.enums.LiveTypeEnum;
import com.netease.nemo.entlive.parameter.LiveListQueryParam;
import com.netease.nemo.parameter.InitUserParam;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @Author：CH
 * @Date：2023/9/4 3:44 PM
 */
@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles(value = "local")
@RunWith(OrderedRunner.class)
public class TestNemoInitController {
    @Autowired
    protected MockMvc mockMvc;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    @Test
    @Order(0)
    public void test_InitUser() throws Exception {
        // 请求地址
        // 请求地址
        String urlTemplate = "/nemo/app/initAppAndUser";
        InitUserParam param = new InitUserParam();
        param.setUserUuid("test111");
        param.setUserName("test111");

        /* 请求头集合 */
        HttpHeaders headers = initHeader();

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

    private HttpHeaders initHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("appkey", yunXinConfigProperties.getAppKey());
        headers.add("AppSecret", yunXinConfigProperties.getAppSecret());
        return headers;
    }
}
