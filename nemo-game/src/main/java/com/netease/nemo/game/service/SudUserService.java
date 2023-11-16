package com.netease.nemo.game.service;

import com.netease.nemo.game.dto.SudLoginDto;
import com.netease.nemo.game.dto.SudUserDto;
import com.netease.nemo.game.response.GetSSTokenResp;
import com.netease.nemo.game.response.UpdateSSTokenResp;
import com.netease.nemo.game.dto.UserClaimDto;

/**
 * @Description：SudUserService
 * @Date：2023/8/11 6:09 PM
 */
public interface SudUserService {

    SudLoginDto sudLogin(String appKey, String userUUid);

    String getCode(String appKey, String userUUid);

    GetSSTokenResp getSSToken(String code);

    UserClaimDto getUserByCode(String code);

    UpdateSSTokenResp updateSSToken(String ssToken);

    SudUserDto getUserInfo(String ssToken);
}
