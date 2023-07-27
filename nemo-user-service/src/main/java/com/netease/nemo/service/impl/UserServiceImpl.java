package com.netease.nemo.service.impl;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.enums.UserStateEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.mapper.UserDeviceMapper;
import com.netease.nemo.mapper.UserMapper;
import com.netease.nemo.model.po.User;
import com.netease.nemo.model.po.UserDevice;
import com.netease.nemo.openApi.NeRoomService;
import com.netease.nemo.openApi.NimService;
import com.netease.nemo.openApi.enums.FriendAddTypeEnum;
import com.netease.nemo.openApi.enums.ImMsgTypeEnum;
import com.netease.nemo.openApi.enums.ImOpeEnum;
import com.netease.nemo.openApi.paramters.neroom.CreateNeRoomUserParam;
import com.netease.nemo.parameter.CreateUserParam;
import com.netease.nemo.parameter.LoginParam;
import com.netease.nemo.parameter.UpdateUserParam;
import com.netease.nemo.service.UserService;
import com.netease.nemo.util.ObjectMapperUtil;
import com.netease.nemo.util.UUIDUtil;
import com.netease.nemo.wrapper.UserMapperWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private NimService nimService;

    @Resource
    private NeRoomService neRoomService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserMapperWrapper userMapperWrapper;

    @Resource
    private UserDeviceMapper userDeviceMapper;

    @Value("${business.systemAccid:nimsystembot_1}")
    private String nimSystemBot;

    @Value("${business.yunxinAssistAccid:yunxinassistaccid_1}")
    private String yunxinAssistAccid;

    @Override
    @Transactional
    public UserDto login(LoginParam loginParam) {
        String userUuid = loginParam.getUserUuid();
        String deviceId = loginParam.getDeviceId();

        User user = userMapperWrapper.selectByUserUuid(userUuid);
        if (user == null) {
            user = ObjectMapperUtil.map(loginParam, User.class);
            user.setUserToken(UUIDUtil.getRandomString(20));
            user.setState(UserStateEnum.NORMAL.getState());
            user.setImToken(UUIDUtil.getRandomString(20));
            userMapperWrapper.insertSelective(user);
        }

        if (BooleanUtils.isTrue(loginParam.getIsCreateImAccId())) {
            nimService.createUser(userUuid, user.getUserName(), user.getIcon(), user.getImToken());
        }
        if (BooleanUtils.isTrue(loginParam.getIsAddFriend())) {
            nimService.addFriend(userUuid, yunxinAssistAccid, FriendAddTypeEnum.ADD_FRIEND.getCode(), "添加云信小秘书");
            nimService.sendImMsg(yunxinAssistAccid, userUuid, ImOpeEnum.SINGLE_CHAT_MESSAGE.getOpe(), ImMsgTypeEnum.TEXT_MESSAGE.getType(), "欢迎来到\"云信派对\"，一场奇妙的旅行就此开始。");
        }

        UserDevice userDevice = getUserDevice(userUuid, deviceId);
        return UserDto.build(user, userDevice);
    }

    @Override
    public UserDto login(String userUuid, String deviceId) {
        User user = userMapperWrapper.selectByUserUuid(userUuid);
        UserDevice userDevice = getUserDevice(userUuid, deviceId);
        return UserDto.build(user, userDevice);
    }


    @Override
    public UserDto getUser(String userUuid) {
        User user = userMapperWrapper.selectByUserUuid(userUuid);
        if (user == null) {
            throw new BsException(ErrorCode.USER_NOT_EXIST);
        }
        return UserDto.build(user);
    }

    @Override
    public List<UserDto> getUsers(List<String> userUuids) {
        if (CollectionUtils.isEmpty(userUuids)) {
            return Collections.emptyList();
        }
        List<User> users = userMapper.selectByUserUuids(userUuids);
        return users.stream().map(UserDto::build).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserByUidAndDeviceId(String userUuid, String deviceId) {
        User user = userMapperWrapper.selectByUserUuid(userUuid);
        UserDevice userDevice = getUserDevice(userUuid, deviceId);
        return UserDto.build(user, userDevice);
    }

    @Override
    public UserDto getUserByRtcUid(Long rtcUid) {
        if (rtcUid == null) {
            throw new BsException(ErrorCode.BAD_REQUEST, "Empty rtcUid");
        }
        UserDevice userDevice = userDeviceMapper.selectByPrimaryKey(rtcUid);
        if (userDevice == null) {
            return null;
        }
        User user = userMapperWrapper.selectByUserUuid(userDevice.getUserUuid());
        if (user == null) {
            return null;
        }
        return UserDto.build(user, userDevice);
    }

    @Override
    public UserDto getUserByMobile(String mobile) {
        User user = userMapperWrapper.getUserByMobile(mobile);
        if (user == null) {
            throw new BsException(ErrorCode.USER_NOT_EXIST);
        }
        return UserDto.build(user);
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserParam createUserParam) {
        String userUuid = createUserParam.getUserUuid();
        User user = userMapperWrapper.selectByUserUuid(userUuid);
        if (user != null) {
            log.info("账号已存在");
            // 不做更新直接返回老账号
            UserDevice userDevice = getUserDevice(userUuid, createUserParam.getDeviceId());
            return UserDto.build(user, userDevice);
        }

        user = ObjectMapperUtil.map(createUserParam, User.class);
        user.setState(UserStateEnum.NORMAL.getState());
        userMapperWrapper.insertSelective(user);
        if (BooleanUtils.isTrue(createUserParam.getIsCreateImAccId())) {
            nimService.createUser(userUuid, user.getUserName(), user.getIcon(), user.getImToken());
        }
        if (BooleanUtils.isTrue(createUserParam.getIsAddFriend())) {
            nimService.addFriend(userUuid, yunxinAssistAccid, FriendAddTypeEnum.ADD_FRIEND.getCode(), "添加云信小秘书");
            nimService.sendImMsg(yunxinAssistAccid, userUuid, ImOpeEnum.SINGLE_CHAT_MESSAGE.getOpe(), ImMsgTypeEnum.TEXT_MESSAGE.getType(), "欢迎来到\"云信派对\"，一场奇妙的旅行就此开始。");
        }
        UserDevice userDevice = getUserDevice(userUuid, createUserParam.getDeviceId());
        return UserDto.build(user, userDevice);
    }

    @Override
    @Transactional
    public void updateUser(UpdateUserParam updateUserParam) {
        User user = userMapperWrapper.selectByUserUuid(updateUserParam.getUserUuid());
        if (user == null) {
            throw new BsException(ErrorCode.USER_NOT_EXIST);
        }

        if (StringUtils.isNotEmpty(updateUserParam.getUserName())) {
            user.setUserName(updateUserParam.getUserName());
        }
        if (StringUtils.isNotEmpty(updateUserParam.getIcon())) {
            user.setIcon(updateUserParam.getIcon());
        }
        if (null != updateUserParam.getAge()) {
            user.setAge(updateUserParam.getAge());
        }
        if (null != updateUserParam.getSex()) {
            user.setSex(updateUserParam.getSex());
        }
        userMapperWrapper.updateByPrimaryKeySelective(user);
    }

    @Override
    @Transactional
    public UserDto createNeRoomUser(CreateUserParam createUserParam) {
        String userUuid = createUserParam.getUserUuid();
        String imToken = createUserParam.getImToken();
        if (StringUtils.isEmpty(imToken)) {
            imToken = UUIDUtil.getUUID();
        }
        String userToken = createUserParam.getUserToken();
        if (StringUtils.isEmpty(userToken)) {
            userToken = UUIDUtil.getUUID();
        }

        User user = userMapperWrapper.selectByUserUuid(userUuid);
        boolean exist = false;
        if (user != null) {
            log.info("账号已存在,使用老账号同步");
            exist = true;
        }

        user = ObjectMapperUtil.map(createUserParam, User.class);
        user.setState(UserStateEnum.NORMAL.getState());
        user.setImToken(imToken);
        user.setUserToken(userToken);

        // 同步NeRoom账号
        CreateNeRoomUserParam neRoomUser = new CreateNeRoomUserParam();
        neRoomUser.setUserName(user.getUserName());
        neRoomUser.setIcon(user.getIcon());
        neRoomUser.setImToken(imToken);
        neRoomUser.setUserToken(userToken);
        neRoomUser.setUpdateOnConflict(true);
        neRoomService.createNeRoomUser(userUuid, neRoomUser);

        if (!exist) {
            userMapperWrapper.insertSelective(user);
        } else {
            userMapperWrapper.updateByPrimaryKeySelective(user);
        }

        return UserDto.build(user);
    }

    private UserDevice getUserDevice(String userUuid, String deviceId) {
        UserDevice userDevice = userDeviceMapper.selectByUserAndDeviceId(userUuid, deviceId);
        if (userDevice == null) {
            userDevice = new UserDevice(userUuid, deviceId);
            userDeviceMapper.insertSelective(userDevice);
        }
        return userDevice;
    }
}