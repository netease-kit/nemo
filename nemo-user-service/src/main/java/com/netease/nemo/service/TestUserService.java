package com.netease.nemo.service;

import com.google.gson.reflect.TypeToken;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.enums.UserStateEnum;
import com.netease.nemo.mapper.UserDeviceMapper;
import com.netease.nemo.mapper.UserMapper;
import com.netease.nemo.model.po.User;
import com.netease.nemo.openApi.NeRoomService;
import com.netease.nemo.openApi.NimService;
import com.netease.nemo.openApi.enums.FriendAddTypeEnum;
import com.netease.nemo.openApi.paramters.neroom.CreateNeRoomUserParam;
import com.netease.nemo.util.UUIDUtil;
import com.netease.nemo.util.gson.GsonUtil;
import com.netease.nemo.wrapper.UserMapperWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 生成测试账号实现, 非业务代码
 *
 * @Author：CH
 * @Date：2023/5/22 1:57 下午
 */
@Service
@Slf4j
public class TestUserService implements InitializingBean {

    @Resource
    private NimService nimService;

    @Resource
    private NeRoomService neRoomService;

    @Resource
    private UserMapperWrapper userMapperWrapper;
    @Resource
    private UserDeviceMapper userDeviceMapper;

    @Value("${business.systemAccid:nimsystembot_1}")
    private String nimSystemBot;

    @Value("${business.yunxinAssistAccid:yunxinassistaccid_1}")
    private String yunxinAssistAccid;

    private Map<String, User> defaultUserMap;

    /**
     * 初始化1v1账号
     *
     * @return List<UserDto>
     */
    public List<UserDto> initOneToOneTestUser() {
        List<UserDto> testUsers = new ArrayList<>();
        if (userMapperWrapper.selectByUserUuid(nimSystemBot) == null) {
            initDefaultUser(defaultUserMap.get(nimSystemBot));
        }
        if (userMapperWrapper.selectByUserUuid(yunxinAssistAccid) == null) {
            initDefaultUser(defaultUserMap.get(yunxinAssistAccid));
        }

        initVoiceRoomUser(testUsers, "yun_xin_party_test1");
        initVoiceRoomUser(testUsers, "yun_xin_party_test2");
        initVoiceRoomUser(testUsers, "yun_xin_party_test3");
        initVoiceRoomUser(testUsers, "yun_xin_party_test4");

        return testUsers;
    }

    /**
     * 初始化NeRoom账号
     * @return
     */
    public List<UserDto> initVoiceRoomUser() {
        List<UserDto> testUsers = new ArrayList<>();
        // 初始化 nimSystemBot
        if (userMapperWrapper.selectByUserUuid(nimSystemBot) == null) {
            initDefaultNeRoomUser(defaultUserMap.get(nimSystemBot));
        }
        // 初始化 yunxinAssistAccid
        if (userMapperWrapper.selectByUserUuid(yunxinAssistAccid) == null) {
            initDefaultNeRoomUser(defaultUserMap.get(yunxinAssistAccid));
        }

        // 添加 yun_xin_party_voice_test1
        initVoiceRoomUser(testUsers,"yun_xin_party_voice_test1");
        // 添加 yun_xin_party_voice_test2
        initVoiceRoomUser(testUsers,"yun_xin_party_voice_test2");
        return testUsers;
    }

    private void initVoiceRoomUser(List<UserDto> testUsers, String userUuid) {
        User user = userMapperWrapper.selectByUserUuid(userUuid);
        if (user == null) {
            testUsers.add(initDefaultNeRoomUser(defaultUserMap.get(userUuid)));
            nimService.addFriend(userUuid, yunxinAssistAccid, FriendAddTypeEnum.ADD_FRIEND.getCode(), "添加云信小秘书");
        } else {
            testUsers.add(UserDto.build(user));
        }
    }

    private UserDto initDefaultUser(String mobile, String userUuid, String useName, String icon) {
        User user = new User();
        user.setUserUuid(userUuid);
        user.setMobile(mobile);
        user.setUserName(useName);
        user.setIcon(icon);
        user.setUserToken(userUuid);
        user.setState(UserStateEnum.NORMAL.getState());
        user.setImToken(userUuid);
        initDefaultUser(user);
        return UserDto.build(user);
    }

    private UserDto initDefaultUser(User user) {
        userMapperWrapper.insertSelective(user);
        nimService.createUser(user.getUserUuid(), user.getUserName(), user.getIcon(), user.getImToken());
        return UserDto.build(user);
    }

    private UserDto initDefaultNeRoomUser(String mobile, String userUuid, String useName, String icon) {
        User user = new User();
        user.setUserUuid(userUuid);
        user.setMobile(mobile);
        user.setUserName(useName);
        user.setIcon(icon);
        user.setUserToken(userUuid);
        user.setState(UserStateEnum.NORMAL.getState());
        user.setImToken(userUuid);
        initDefaultNeRoomUser(user);
        return UserDto.build(user);
    }

    private UserDto initDefaultNeRoomUser(User user) {
        userMapperWrapper.insertSelective(user);
        neRoomService.createNeRoomUser(user.getUserUuid(), CreateNeRoomUserParam.builder().userName(user.getUserName()).icon(user.getIcon()).userToken(user.getUserToken()).imToken(user.getImToken()).updateOnConflict(true).build());
        return UserDto.build(user);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<User> defaultUsers = GsonUtil.fromJson(initUserJson, new TypeToken<List<User>>() {
        }.getType());
        defaultUserMap = defaultUsers.stream().collect(Collectors.toMap(User::getUserUuid, o -> o));
    }

    private String initUserJson = "[{\"mobile\":\"10333333331\",\"userUuid\":\"nimsystembot_1\",\"userName\":\"云信派对后台账号\",\"userToken\":\"nimsystembot_1\",\"imToken\":\"nimsystembot_1\",\"icon\":\"https://yx-web-nosdn.netease.im/common/d806ef46c9e6f53c73545786e76a5648/POPO20230419-204253.png\",\"state\":1},{\"mobile\":\"10333333332\",\"userUuid\":\"yunxinassistaccid_1\",\"userName\":\"云信派对小秘书\",\"userToken\":\"yunxinassistaccid_1\",\"imToken\":\"yunxinassistaccid_1\",\"icon\":\"https://yx-web-nosdn.netease.im/common/d806ef46c9e6f53c73545786e76a5648/POPO20230419-204253.png\",\"state\":1},{\"mobile\":\"10333333333\",\"userUuid\":\"yun_xin_party_test1\",\"userName\":\"yun_xin_party_test1\",\"userToken\":\"yun_xin_party_test1\",\"imToken\":\"yun_xin_party_test1\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117781702971392.jpg\",\"state\":1},{\"mobile\":\"10333333334\",\"userUuid\":\"yun_xin_party_test2\",\"userName\":\"yun_xin_party_test2\",\"userToken\":\"yun_xin_party_test2\",\"imToken\":\"yun_xin_party_test2\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117782030127104.jpg\",\"state\":1},{\"mobile\":\"10333333335\",\"userUuid\":\"yun_xin_party_test3\",\"userName\":\"yun_xin_party_test3\",\"userToken\":\"yun_xin_party_test3\",\"imToken\":\"yun_xin_party_test3\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117781702971392.jpg\",\"state\":1},{\"mobile\":\"10333333336\",\"userUuid\":\"yun_xin_party_test4\",\"userName\":\"yun_xin_party_test4\",\"userToken\":\"yun_xin_party_test4\",\"imToken\":\"yun_xin_party_test4\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117782030127104.jpg\",\"state\":1},{\"mobile\":\"10444444441\",\"userUuid\":\"yun_xin_party_voice_test1\",\"userName\":\"yun_xin_party_voice_test1\",\"userToken\":\"yun_xin_party_voice_test1\",\"imToken\":\"yun_xin_party_voice_test1\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117780306268160.jpg\",\"state\":1},{\"mobile\":\"10444444442\",\"userUuid\":\"yun_xin_party_voice_test2\",\"userName\":\"yun_xin_party_voice_test2\",\"userToken\":\"yun_xin_party_voice_test2\",\"imToken\":\"yun_xin_party_voice_test2\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117772718772224.jpg\",\"state\":1},{\"mobile\":\"10444444443\",\"userUuid\":\"yun_xin_party_voice_test3\",\"userName\":\"yun_xin_party_voice_test3\",\"userToken\":\"yun_xin_party_voice_test3\",\"imToken\":\"yun_xin_party_voice_test3\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117772718772224.jpg\",\"state\":1}]";
}
