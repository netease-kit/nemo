package com.netease.nemo.service.impl;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.enums.UserStateEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.mapper.UserDeviceMapper;
import com.netease.nemo.mapper.UserMapper;
import com.netease.nemo.model.po.User;
import com.netease.nemo.model.po.UserDevice;
import com.netease.nemo.openApi.NimService;
import com.netease.nemo.openApi.enums.FriendAddTypeEnum;
import com.netease.nemo.openApi.enums.ImMsgTypeEnum;
import com.netease.nemo.openApi.enums.ImOpeEnum;
import com.netease.nemo.parameter.CreateUserParam;
import com.netease.nemo.parameter.LoginParam;
import com.netease.nemo.parameter.UpdateUserParam;
import com.netease.nemo.service.UserService;
import com.netease.nemo.util.ObjectMapperUtil;
import com.netease.nemo.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private NimService nimService;

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserDeviceMapper userDeviceMapper;

    @Value("${business.systemAccid:nimsystembot_1}")
    private String nimSystemBot;

    @Value("${business.yunxinAssistAccid:yunxinassistaccid_1}")
    private String yunxinAssistAccid;

    @Override
    public UserDto login(LoginParam loginParam) {
        String userUuid = loginParam.getUserUuid();
        String deviceId = loginParam.getDeviceId();

        User user = userMapper.selectByUserUuid(userUuid);
        if (user == null) {
            user = ObjectMapperUtil.map(loginParam, User.class);
            user.setUserToken(UUIDUtil.getRandomString(20));
            user.setState(UserStateEnum.NORMAL.getState());
            user.setImToken(UUIDUtil.getRandomString(20));
            userMapper.insertSelective(user);
        }

        if(BooleanUtils.isTrue(loginParam.getIsCreateImAccId())) {
            nimService.createUser(userUuid, user.getUserName(), user.getIcon(), user.getImToken());
        }
        if(BooleanUtils.isTrue(loginParam.getIsAddFriend())) {
            nimService.addFriend(userUuid, yunxinAssistAccid, FriendAddTypeEnum.ADD_FRIEND.getCode(), "添加云信小秘书");
            nimService.sendImMsg(yunxinAssistAccid, userUuid, ImOpeEnum.SINGLE_CHAT_MESSAGE.getOpe(), ImMsgTypeEnum.TEXT_MESSAGE.getType(), "欢迎来到\"云信派对\"，一场奇妙的旅行就此开始。");
        }

        UserDevice userDevice = getUserDevice(userUuid, deviceId);
        return UserDto.build(user, userDevice);
    }

    @Override
    public UserDto login(String userUuid, String deviceId) {
        User user = userMapper.selectByUserUuid(userUuid);
        UserDevice userDevice = getUserDevice(userUuid, deviceId);
        return UserDto.build(user, userDevice);
    }


    @Override
    public UserDto getUser(String userUuid) {
        User user = userMapper.selectByUserUuid(userUuid);
        if (user == null) {
            throw  new BsException(ErrorCode.USER_NOT_EXIST);
        }
        return UserDto.build(user);
    }

    @Override
    public UserDto getUserByUidAndDeviceId(String userUuid, String deviceId) {
        User user = userMapper.selectByUserUuid(userUuid);
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
        User user = userMapper.selectByUserUuid(userDevice.getUserUuid());
        if (user == null) {
            return null;
        }
        return UserDto.build(user, userDevice);
    }

    @Override
    public UserDto getUserByMobile(String mobile) {
        User user = userMapper.getUserByMobile(mobile);
        if (user == null) {
            throw  new BsException(ErrorCode.USER_NOT_EXIST);
        }
        return UserDto.build(user);
    }

    @Override
    public UserDto createUser(CreateUserParam createUserParam) {
        String userUuid = createUserParam.getUserUuid();
        User user = userMapper.selectByUserUuid(userUuid);
        if (user != null) {
            log.info("账号已存在");
            UserDevice userDevice = getUserDevice(userUuid, createUserParam.getDeviceId());
            return UserDto.build(user, userDevice);
        }

        user = ObjectMapperUtil.map(createUserParam, User.class);
        user.setState(UserStateEnum.NORMAL.getState());
        userMapper.insertSelective(user);
        if(BooleanUtils.isTrue(createUserParam.getIsCreateImAccId())) {
            nimService.createUser(userUuid, user.getUserName(), user.getIcon(), user.getImToken());
        }
        if(BooleanUtils.isTrue(createUserParam.getIsAddFriend())) {
            nimService.addFriend(userUuid, yunxinAssistAccid, FriendAddTypeEnum.ADD_FRIEND.getCode(), "添加云信小秘书");
            nimService.sendImMsg(yunxinAssistAccid, userUuid, ImOpeEnum.SINGLE_CHAT_MESSAGE.getOpe(), ImMsgTypeEnum.TEXT_MESSAGE.getType(), "欢迎来到\"云信派对\"，一场奇妙的旅行就此开始。");
        }
        UserDevice userDevice = getUserDevice(userUuid, createUserParam.getDeviceId());
        return UserDto.build(user, userDevice);
    }

    @Override
    public void updateUser(UpdateUserParam updateUserParam) {
        User user = userMapper.selectByUserUuid(updateUserParam.getUserUuid());
        if (user == null) {
            throw  new BsException(ErrorCode.USER_NOT_EXIST);
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
        userMapper.updateByPrimaryKey(user);
    }

    @Override
    public List<UserDto> initOneToOneTestUser() {
        List<UserDto> testUsers = new ArrayList<>();
        // 初始化 nimSystemBot
        if (userMapper.selectByUserUuid(nimSystemBot) == null) {
            initDefaultUser("10333333331", nimSystemBot, "云信派对后台账号", "https://yx-web-nosdn.netease.im/common/d806ef46c9e6f53c73545786e76a5648/POPO20230419-204253.png");
        }
        // 初始化 yunxinAssistAccid
        if(userMapper.selectByUserUuid(yunxinAssistAccid) == null) {
            initDefaultUser("10333333332", yunxinAssistAccid, "云信派对小秘书","https://yx-web-nosdn.netease.im/common/d806ef46c9e6f53c73545786e76a5648/POPO20230419-204253.png");
        }

        // 初始化 yunXinPartyTest1
        User yunXinPartyTest1 = userMapper.selectByUserUuid("yunxin_party_test1");
        if (yunXinPartyTest1 == null) {
            testUsers.add(initDefaultUser("11333333333", "yunxin_party_test1", "yunxin_party_test1", ""));
            nimService.addFriend("yunXinPartyTest1", yunxinAssistAccid, FriendAddTypeEnum.ADD_FRIEND.getCode(), "添加云信小秘书");
        } else {
            testUsers.add(UserDto.build(yunXinPartyTest1));
        }

        // 初始化 yunXinPartyTest2
        User yunXinPartyTest2 = userMapper.selectByUserUuid("yunxin_party_test2");
        if (yunXinPartyTest2 == null) {
            testUsers.add(initDefaultUser("11333333334", "yunxin_party_test2", "yunxin_party_test2", ""));
            nimService.addFriend("yunxin_party_test2", yunxinAssistAccid, FriendAddTypeEnum.ADD_FRIEND.getCode(), "添加云信小秘书");
        } else {
            testUsers.add(UserDto.build(yunXinPartyTest2));
        }

        return testUsers;
    }

    private UserDto initDefaultUser(String mobile, String userUuid, String useName, String icon) {
        User user = new User();
        user.setUserUuid(userUuid);
        user.setMobile(mobile);
        user.setUserName(useName);
        user.setIcon(icon);
        user.setUserToken(UUIDUtil.getRandomString(20));
        user.setState(UserStateEnum.NORMAL.getState());
        user.setImToken(UUIDUtil.getRandomString(20));
        userMapper.insertSelective(user);
        nimService.createUser(userUuid, useName, icon, user.getImToken());
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
