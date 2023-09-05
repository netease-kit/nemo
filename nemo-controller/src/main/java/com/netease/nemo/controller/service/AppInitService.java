package com.netease.nemo.controller.service;

import com.google.gson.reflect.TypeToken;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.entlive.mapper.LiveRecordMapper;
import com.netease.nemo.entlive.service.EntLiveService;
import com.netease.nemo.entlive.util.LiveResourceUtil;
import com.netease.nemo.enums.UserStateEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.mapper.UserDeviceMapper;
import com.netease.nemo.model.po.User;
import com.netease.nemo.openApi.NeRoomService;
import com.netease.nemo.openApi.NimService;
import com.netease.nemo.openApi.enums.FriendAddTypeEnum;
import com.netease.nemo.openApi.paramters.neroom.CreateNeRoomUserParam;
import com.netease.nemo.parameter.InitUserParam;
import com.netease.nemo.socialchat.service.OneToOneChatService;
import com.netease.nemo.util.UUIDUtil;
import com.netease.nemo.util.gson.GsonUtil;
import com.netease.nemo.wrapper.UserMapperWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
public class AppInitService implements InitializingBean {

    @Resource
    private NimService nimService;

    @Resource
    private NeRoomService neRoomService;

    @Resource
    private UserMapperWrapper userMapperWrapper;

    @Resource
    private UserDeviceMapper userDeviceMapper;

    @Resource
    private EntLiveService entLiveService;

    @Resource
    private LiveRecordMapper liveRecordMapper;

    @Value("${business.systemAccid:nimsystembot_1}")
    private String nimSystemBot;

    @Value("${business.yunxinAssistAccid:yunxinassistaccid_1}")
    private String yunxinAssistAccid;

    private Map<String, User> defaultUserMap;

    @Resource
    private OneToOneChatService oneToOneChatService;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;


    public UserDto initUser(User user, Integer sceneType) {
        UserDto userDto = null;
        User existUser = userMapperWrapper.selectByUserUuid(user.getUserUuid());
        if (existUser != null) {
            return UserDto.build(user);
        }

        if (sceneType == null || 1 == sceneType) {
            userDto = initImUser(user);
            nimService.addFriend(user.getUserUuid(), yunxinAssistAccid, FriendAddTypeEnum.ADD_FRIEND.getCode(), "添加云信小秘书");
        } else if (2 == sceneType) {
            userDto = initDefaultNeRoomUser(user);
            nimService.addFriend(user.getUserUuid(), yunxinAssistAccid, FriendAddTypeEnum.ADD_FRIEND.getCode(), "添加云信小秘书");
        }
        return userDto;
    }

    private UserDto initDefaultUser(User user) {
        userMapperWrapper.insertSelective(user);
        nimService.createUser(user.getUserUuid(), user.getUserName(), user.getIcon(), user.getImToken());
        return UserDto.build(user);
    }

    private UserDto initDefaultNeRoomUser(User user) {
        userMapperWrapper.insertSelective(user);
        neRoomService.createNeRoomUser(user.getUserUuid(), CreateNeRoomUserParam.builder().userName(user.getUserName()).icon(user.getIcon()).userToken(user.getUserToken()).imToken(user.getImToken()).updateOnConflict(true).build());
        return UserDto.build(user);
    }

    private UserDto initImUser(User user) {
        userMapperWrapper.insertSelective(user);
        nimService.createUser(user.getUserUuid(), user.getUserName(), user.getIcon(), user.getImToken());
        return UserDto.build(user);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String initUserJson = "[{\"mobile\":\"10333333331\",\"userUuid\":\"nimsystembot_1\",\"userName\":\"云信派对后台账号\",\"icon\":\"https://yx-web-nosdn.netease.im/common/d806ef46c9e6f53c73545786e76a5648/POPO20230419-204253.png\",\"state\":1},{\"mobile\":\"10333333332\",\"userUuid\":\"yunxinassistaccid_1\",\"userName\":\"云信派对小秘书\",\"icon\":\"https://yx-web-nosdn.netease.im/common/d806ef46c9e6f53c73545786e76a5648/POPO20230419-204253.png\",\"state\":1},{\"mobile\":\"10333333333\",\"userUuid\":\"yunxin_party_test1\",\"userName\":\"yunxin_party_test1\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117781702971392.jpg\",\"state\":1},{\"mobile\":\"10333333334\",\"userUuid\":\"yunxin_party_test2\",\"userName\":\"yunxin_party_test2\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117782030127104.jpg\",\"state\":1},{\"mobile\":\"10333333335\",\"userUuid\":\"yunxin_party_test3\",\"userName\":\"yunxin_party_test3\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117781702971392.jpg\",\"state\":1},{\"mobile\":\"10333333336\",\"userUuid\":\"yunxin_party_test4\",\"userName\":\"yunxin_party_test4\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117782030127104.jpg\",\"state\":1},{\"mobile\":\"10444444441\",\"userUuid\":\"yunxin_party_voice_test1\",\"userName\":\"yunxin_party_voice_test1\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117780306268160.jpg\",\"state\":1},{\"mobile\":\"10444444442\",\"userUuid\":\"yunxin_party_voice_test2\",\"userName\":\"yunxin_party_voice_test2\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117772718772224.jpg\",\"state\":1},{\"mobile\":\"10444444443\",\"userUuid\":\"yunxin_party_voice_test3\",\"userName\":\"yunxin_party_voice_test3\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117772718772224.jpg\",\"state\":1},{\"mobile\":\"10444444444\",\"userUuid\":\"virtually_voice_user_3\",\"userName\":\"酥酥\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117790972383232.jpg\",\"state\":1},{\"mobile\":\"10444444445\",\"userUuid\":\"virtually_voice_user_4\",\"userName\":\"蜜桃味\",\"icon\":\"https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/86117793388302336.jpg\",\"state\":1}]";
        List<User> defaultUsers = GsonUtil.fromJson(initUserJson, new TypeToken<List<User>>() {
        }.getType());
        defaultUserMap = defaultUsers.stream().collect(Collectors.toMap(User::getUserUuid, o -> o));
    }

    /**
     * 初始化APP和用户
     *
     * @param appKey   appKey
     * @param initUserParam 用户昵称
     * @return UserDto
     */
    public UserDto initAppAndUser(String appKey, InitUserParam initUserParam) {
        if(StringUtils.isEmpty(appKey)) {
            throw new BsException(ErrorCode.BAD_REQUEST, "appKey不能为空");
        }
        
        // 初始化系统用户
        initSystemUser();
        // 初始化一对一虚拟用户
        oneToOneChatService.initOneOneVirtuallyUser();

        User user = buildUser(initUserParam);
        return initUser(user, initUserParam.getSceneType());
    }

    private void initSystemUser() {
        if (userMapperWrapper.selectByUserUuid(nimSystemBot)== null) {
            User systemBot = defaultUserMap.get(nimSystemBot);
            String token = UUIDUtil.getRandomString(24);
            systemBot.setImToken(token);
            systemBot.setUserToken(token);
            initDefaultUser(systemBot);
        }
        if (userMapperWrapper.selectByUserUuid(yunxinAssistAccid) == null) {
            User yunXinAssist = defaultUserMap.get(yunxinAssistAccid);
            String token = UUIDUtil.getRandomString(24);
            yunXinAssist.setImToken(token);
            yunXinAssist.setUserToken(token);
            initDefaultUser(yunXinAssist);
        }
    }

    private  User buildUser(InitUserParam initUserParam) {
        User user = new User();
        user.setUserName(StringUtils.isEmpty(initUserParam.getUserName()) ? LiveResourceUtil.getRandomUserName() : initUserParam.getUserName());
        user.setUserUuid(StringUtils.isEmpty(initUserParam.getUserUuid()) ? UUIDUtil.randomLowerLetterAndNumber(32) : initUserParam.getUserUuid());
        user.setIcon(StringUtils.isEmpty(initUserParam.getIcon()) ? LiveResourceUtil.getRandomAvatar() : initUserParam.getIcon());
        String token = UUIDUtil.getRandomString(24);
        user.setImToken(StringUtils.isEmpty(initUserParam.getImToken()) ? token : initUserParam.getImToken());
        user.setUserToken(StringUtils.isEmpty(initUserParam.getUserToken()) ? token : initUserParam.getUserToken());
        user.setAge(-1);
        user.setMobile(UUIDUtil.getRandomNumber(11));
        user.setSex(-1);
        user.setState(UserStateEnum.NORMAL.getState());
        return user;
    }
}
