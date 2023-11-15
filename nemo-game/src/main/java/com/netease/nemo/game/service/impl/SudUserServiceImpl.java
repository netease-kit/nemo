package com.netease.nemo.game.service.impl;

import com.netease.nemo.dto.UserDto;
import com.netease.nemo.game.dto.SudLoginDto;
import com.netease.nemo.game.dto.SudUserDto;
import com.netease.nemo.game.dto.TokenResponse;
import com.netease.nemo.game.response.GetSSTokenResp;
import com.netease.nemo.game.response.UpdateSSTokenResp;
import com.netease.nemo.game.service.SudUserService;
import com.netease.nemo.game.util.JwtUtil;
import com.netease.nemo.service.UserService;
import com.netease.nemo.game.dto.UserClaimDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description：SudUserService
 * @Date：2023/8/11 6:09 PM
 */
@Service
@Slf4j
public class SudUserServiceImpl implements SudUserService {

    @Value("${business.game.appId}")
    private String sudAppId;
    @Value("${business.game.appKey}")
    private String sudAppKey;

    @Value("${business.game.appSecret}")
    private String appSecret;

    @Resource
    private UserService userService;

    @Override
    public SudLoginDto sudLogin(String appKey, String userUUid) {
        String code = getCode(appKey, userUUid);
        return SudLoginDto.builder()
                .code(code)
                .appId(sudAppId)
                .appKey(sudAppKey)
                .build();
    }

    public String getCode(String appKey, String userUUid) {
        return JwtUtil.createToken(appKey, userUUid, sudAppId, appSecret);
    }

    public GetSSTokenResp getSSToken(String code) {
        UserClaimDto userClaimDto = getUserByCode(code);
        TokenResponse token = JwtUtil.createSSToken(userClaimDto.getAppKey(), userClaimDto.getUserUuid(), sudAppId, appSecret, JwtUtil.DEFAULT_MIN_SS_TOKEN_EXPIRE_DURATION);

        UserDto userDto = userService.getUser(userClaimDto.getUserUuid());
        return GetSSTokenResp.builder()
                .ssToken(token.getSsToken())
                .expireDate(token.getExpireDate())
                .userInfo(new SudUserDto(userDto))
                .build();
    }


    public UserClaimDto getUserByCode(String code) {
        return JwtUtil.verifyToken(appSecret, code);
    }

    public UpdateSSTokenResp updateSSToken(String ssToken) {
        UserClaimDto userClaimDto = getUserByCode(ssToken);
        TokenResponse token = JwtUtil.createSSToken(userClaimDto.getAppKey(), userClaimDto.getUserUuid(), sudAppId, appSecret, JwtUtil.DEFAULT_MIN_SS_TOKEN_EXPIRE_DURATION);

        return UpdateSSTokenResp.builder()
                .ssToken(token.getSsToken())
                .expireDate(token.getExpireDate())
                .build();
    }

    public SudUserDto getUserInfo(String ssToken) {
        UserClaimDto userClaimDto = getUserByCode(ssToken);
        String userUuid = userClaimDto.getUserUuid();

        UserDto userDto = userService.getUser(userUuid);
        return new SudUserDto(userDto);
    }
}
