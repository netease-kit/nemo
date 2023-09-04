package com.netease.nemo.controller.socialChat;

import com.netease.nemo.annotation.Checksum;
import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.context.Context;
import com.netease.nemo.controller.service.AppInitService;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.locker.LockerService;
import com.netease.nemo.openApi.NimService;
import com.netease.nemo.openApi.enums.ImMsgTypeEnum;
import com.netease.nemo.openApi.enums.ImOpeEnum;
import com.netease.nemo.parameter.CreateUserParam;
import com.netease.nemo.parameter.UpdateUserParam;
import com.netease.nemo.service.UserService;
import com.netease.nemo.socialchat.dto.OnLineUserDto;
import com.netease.nemo.socialchat.service.OneToOneChatService;
import com.netease.nemo.socialchat.util.OneOneResourceUtil;
import com.netease.nemo.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.netease.nemo.enums.RedisKeyEnum.ONE_ONE_ONLINE_USER_KEY;
import static com.netease.nemo.enums.RedisKeyEnum.TAG_USER_LOCK;


@RestController
@RequestMapping("/nemo/socialChat/server/user")
@Slf4j
@RestResponseBody
public class SocialChatUserServerController {

    @Resource
    private UserService userService;

    @Resource
    private OneToOneChatService oneToOneChatService;

    private AppInitService appInitService;

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    @Resource(name = "redisDistributeLockerImpl")
    private LockerService lockerService;

    @Resource
    private NimService nimService;

    @Checksum
    @PostMapping(value = "/createUser")
    public Object createUser(@Valid @RequestBody CreateUserParam createUserParam) {
        UserDto userDto = userService.createUser(createUserParam);

        String userUuid = userDto.getUserUuid();
        try {
            nimService.sendImMsg("one_one_virtually_user1", userUuid, ImOpeEnum.SINGLE_CHAT_MESSAGE.getOpe(), ImMsgTypeEnum.TEXT_MESSAGE.getType(), "很高兴认识你！");
            nimService.sendImMsg("one_one_virtually_user1", userUuid, ImOpeEnum.SINGLE_CHAT_MESSAGE.getOpe(), ImMsgTypeEnum.PIC_MESSAGE.getType(), OneOneResourceUtil.getMessageImageJson().getAsJsonObject("one_one_virtually_user1"));

            nimService.sendImMsg("one_one_virtually_user2", userUuid, ImOpeEnum.SINGLE_CHAT_MESSAGE.getOpe(), ImMsgTypeEnum.PIC_MESSAGE.getType(), OneOneResourceUtil.getMessageImageJson().getAsJsonObject("one_one_virtually_user2"));
            nimService.sendImMsg("one_one_virtually_user2", userUuid, ImOpeEnum.SINGLE_CHAT_MESSAGE.getOpe(), ImMsgTypeEnum.AUDIO_MESSAGE.getType(), OneOneResourceUtil.getAudioJson().getAsJsonObject("one_one_virtually_user2"));

            nimService.sendImMsg("one_one_virtually_user3", userUuid, ImOpeEnum.SINGLE_CHAT_MESSAGE.getOpe(), ImMsgTypeEnum.AUDIO_MESSAGE.getType(), OneOneResourceUtil.getAudioJson().getAsJsonObject("one_one_virtually_user3"));

            nimService.sendImMsg("one_one_virtually_user4", userUuid, ImOpeEnum.SINGLE_CHAT_MESSAGE.getOpe(), ImMsgTypeEnum.TEXT_MESSAGE.getType(), "哈喽，我们好像很有缘，在线等你回应中~");

        } catch (Exception e) {
            log.error("机器人发送消息失败");
        }
        return userDto;
    }

    @Checksum
    @PostMapping(value = "/updateUser")
    public void updateUser(@Valid @RequestBody UpdateUserParam updateUserParam) {
        String appKey = Context.get().getAppKey();
        lockerService.tryLockAndDo(() -> {
            userService.updateUser(updateUserParam);
            updateReportUser(appKey, updateUserParam);
        }, TAG_USER_LOCK.getKeyPrefix(), appKey, updateUserParam.getUserUuid());
    }

    private void updateReportUser(String appKey, UpdateUserParam updateUserParam) {
        String userKey = RedisUtil.springCacheJoinKey(ONE_ONE_ONLINE_USER_KEY.getKeyPrefix(), appKey, updateUserParam.getUserUuid());
        OnLineUserDto onLineUser = (OnLineUserDto) nemoRedisTemplate.opsForValue().get(userKey);
        if (onLineUser != null) {
            if (StringUtils.isNotEmpty(updateUserParam.getUserName())) {
                onLineUser.setUserName(updateUserParam.getUserName());
            }
            if (StringUtils.isNotEmpty(updateUserParam.getIcon())) {
                onLineUser.setIcon(updateUserParam.getIcon());
            }
            nemoRedisTemplate.opsForValue().set(userKey, onLineUser);
        }
    }
}
