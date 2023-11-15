package com.netease.nemo.controller.sud;

import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.annotation.TokenAuth;
import com.netease.nemo.context.Context;
import com.netease.nemo.game.service.SudUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 忽然平台 Sud 游戏登录相关接口
 *
 * @Author：CH
 * @Date：2023/8/14 10:25 AM
 */
@RestController
@RequestMapping("/nemo/game/sud")
@Slf4j
@RestResponseBody
public class SudLoginController {

    @Resource
    private SudUserService sudUserService;

    /**
     * 登录接口，获取针对当前用户(UID)的短期令牌Code
     * 调用方：接入端APP
     *
     * @return
     */
    @PostMapping("/login")
    @TokenAuth
    public Object getCode() {
        return sudUserService.sudLogin(Context.get().getAppKey(), Context.get().getUserUuid());
    }
}
