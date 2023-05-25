package com.netease.nemo.controller.socialChat;

import com.netease.nemo.annotation.Checksum;
import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.locker.LockerService;
import com.netease.nemo.parameter.CreateUserParam;
import com.netease.nemo.parameter.UpdateUserParam;
import com.netease.nemo.service.UserService;
import com.netease.nemo.socialchat.dto.OnLineUserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.netease.nemo.enums.RedisKeyEnum.TAG_USER_LOCK;


@RestController
@RequestMapping("/nemo/socialChat/server/user")
@Slf4j
@RestResponseBody
public class UserServerController {

    @Resource
    private UserService userService;

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    @Resource(name = "redisDistributeLockerImpl")
    private LockerService lockerService;

    @Checksum
    @PostMapping(value = "/createUser")
    public Object createUser(@Valid @RequestBody CreateUserParam createUserParam) {
        return userService.createUser(createUserParam);
    }

    @Checksum
    @PostMapping(value = "/updateUser")
    public void updateUser(@Valid @RequestBody UpdateUserParam updateUserParam) {
        lockerService.tryLockAndDo(() -> {
            userService.updateUser(updateUserParam);
            updateReportUser(updateUserParam);
        }, TAG_USER_LOCK.getKeyPrefix(), updateUserParam.getUserUuid());
    }

    private void updateReportUser(UpdateUserParam updateUserParam) {
        String key = RedisKeyEnum.ONE_ONE_ONLINE_USER_KEY.getKeyPrefix();
        String filedKey = key + ":" + updateUserParam.getUserUuid();
        OnLineUserDto onLineUser = (OnLineUserDto) nemoRedisTemplate.opsForValue().get(filedKey);
        if (onLineUser != null) {
            if (StringUtils.isNotEmpty(updateUserParam.getUserName())) {
                onLineUser.setUserName(updateUserParam.getUserName());
            }
            if (StringUtils.isNotEmpty(updateUserParam.getIcon())) {
                onLineUser.setIcon(updateUserParam.getIcon());
            }
            nemoRedisTemplate.opsForValue().set(filedKey, onLineUser);
        }
    }

    /**
     * TODO 注： 仅体验demo阶段使用，实际落地该API需删除
     */
    @PostMapping(value = "/initOneToOne")
    public Object initOneToOne() {
        return userService.initOneToOneTestUser();
    }
}
