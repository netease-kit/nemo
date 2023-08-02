package com.netease.nemo.controller;

import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.parameter.InitUserParam;
import com.netease.nemo.service.TestUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Nemo 初始化控制器{初始化账号等}
 *
 * @Author：CH
 * @Date：2023/7/20 2:00 PM
 */
@RestController
@RequestMapping("/nemo/app/")
@Slf4j
@RestResponseBody
public class NemoInitController {

    @Resource
    private TestUserService testUserService;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    @PostMapping(value = "/initAppAndUser")
    public UserDto initAppAndUser(@Valid @RequestBody InitUserParam initUserParam, HttpServletRequest request) {

        String appSecret = request.getHeader("AppSecret");
        if (StringUtils.isEmpty(appSecret) || !yunXinConfigProperties.getAppSecret().equals(appSecret)) {
            log.error("NemoInitController : appSecret is empty or Error");
            throw new BsException(ErrorCode.BAD_REQUEST, "appSecret is empty or error");
        }

        return testUserService.initAppAndUser(initUserParam);
    }

}
