package com.netease.nemo.service;

import com.netease.nemo.dto.UserDto;
import com.netease.nemo.parameter.CreateUserParam;
import com.netease.nemo.parameter.LoginParam;
import com.netease.nemo.parameter.UpdateUserParam;

import java.util.List;

public interface UserService {

    UserDto login(LoginParam loginParam);

    UserDto login(String userUuid, String deviceId);

    UserDto getUser(String userUuid);

    UserDto getUserByUidAndDeviceId(String userUuid, String deviceId);

    UserDto getUserByRtcUid(Long rtcUid);

    UserDto getUserByMobile(String mobile);

    UserDto createUser(CreateUserParam createUserParam);

    void updateUser(UpdateUserParam updateUserParam);

    /**
     * 注： 云信派对1v1娱乐demo体验账号初始化
     *
     * @return 返回测试账号
     */
    List<UserDto> initOneToOneTestUser();
}
