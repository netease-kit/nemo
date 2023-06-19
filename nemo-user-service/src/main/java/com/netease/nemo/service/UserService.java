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

    List<UserDto> getUsers(List<String> userUuids);

    UserDto getUserByUidAndDeviceId(String userUuid, String deviceId);

    UserDto getUserByRtcUid(Long rtcUid);

    UserDto getUserByMobile(String mobile);

    UserDto createUser(CreateUserParam createUserParam);

    void updateUser(UpdateUserParam updateUserParam);

    /**
     * 创建NeRoom账号
     * @param createUserParam 账户信息
     * @return 返回UserDto
     */
    UserDto createNeRoomUser(CreateUserParam createUserParam);
}
