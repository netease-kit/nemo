package com.netease.nemo.controller.sud;

import com.netease.nemo.annotation.SudRestResponseBody;
import com.netease.nemo.annotation.SudSignature;
import com.netease.nemo.game.request.GetSSTokenParam;
import com.netease.nemo.game.request.GetUserInfoParam;
import com.netease.nemo.game.request.UpdateSSTokenParam;
import com.netease.nemo.game.service.SudUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 忽然平台 Sud 游戏 Sud 用户相关接口
 *
 * @Author：CH
 * @Date：2023/8/14 10:25 AM
 */
@RequestMapping("/nemo/game/sud/user")
@Slf4j
@SudRestResponseBody
@RestController
public class SudUserController {

    @Resource
    private SudUserService sudUserService;

    /**
     * 短期令牌Code更换长期令牌SSToken
     * 调用方：游戏服务
     *
     * @param getSSTokenReq
     * @return
     */
    @PostMapping("/get_sstoken")
    @SudSignature
    public Object getSSToken(@RequestBody GetSSTokenParam getSSTokenReq) {
        return sudUserService.getSSToken(getSSTokenReq.getCode());
    }

    /**
     * 刷新长期令牌
     * 调用方：游戏服务
     *
     * @param reqParam
     * @return
     */
    @PostMapping("/update_sstoken")
    @SudSignature
    public Object updateSSToken(@RequestBody UpdateSSTokenParam reqParam) {
        return sudUserService.updateSSToken(reqParam.getSsToken());
    }

    /**
     * 获取用户信息
     * 调用方：游戏服务
     *
     * @param reqParam
     * @return
     */
    @PostMapping("/get_user_info")
    @SudSignature
    public Object getUserInfo(@RequestBody GetUserInfoParam reqParam) {
        return sudUserService.getUserInfo(reqParam.getSsToken());
    }
}
