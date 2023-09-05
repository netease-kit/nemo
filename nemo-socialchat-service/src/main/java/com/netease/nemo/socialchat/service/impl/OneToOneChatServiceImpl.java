package com.netease.nemo.socialchat.service.impl;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.context.Context;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.mapper.UserMapper;
import com.netease.nemo.model.po.Gift;
import com.netease.nemo.model.po.User;
import com.netease.nemo.model.po.UserReward;
import com.netease.nemo.parameter.CreateUserParam;
import com.netease.nemo.service.UserService;
import com.netease.nemo.socialchat.dto.OnLineUserDto;
import com.netease.nemo.socialchat.dto.UserRewardDto;
import com.netease.nemo.socialchat.dto.rtc.RtcRoomInfoDto;
import com.netease.nemo.socialchat.dto.rtc.RtcRoomUserInfoDto;
import com.netease.nemo.socialchat.parameter.rtcNotify.RtcRoomNotifyParam;
import com.netease.nemo.socialchat.parameter.rtcNotify.RtcRoomUserNotifyParam;
import com.netease.nemo.socialchat.service.OneToOneChatService;
import com.netease.nemo.socialchat.service.SocialChatMessageService;
import com.netease.nemo.socialchat.util.OneOneResourceUtil;
import com.netease.nemo.util.ObjectMapperUtil;
import com.netease.nemo.util.RedisUtil;
import com.netease.nemo.util.UUIDUtil;
import com.netease.nemo.wrapper.GiftMapperWrapper;
import com.netease.nemo.wrapper.UserRewardMapperWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.netease.nemo.enums.RedisKeyEnum.ONE_ONE_CHAT_RTC_USER_RECORD_KEY;
import static com.netease.nemo.enums.RedisKeyEnum.RTC_RECORD_KEY;

@Service
@Slf4j
public class OneToOneChatServiceImpl implements OneToOneChatService {

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private SocialChatMessageService socialChatMessageService;

    @Resource
    private UserRewardMapperWrapper userRewardMapperWrapper;

    @Resource
    private GiftMapperWrapper giftMapperWrapper;

    @Override
    public void reporter(String appKey, String userUuid, String deviceId) {
        String userKey = RedisUtil.springCacheJoinKey(RedisKeyEnum.ONE_ONE_ONLINE_USER_KEY.getKeyPrefix(), appKey, userUuid);
        String orderKey = RedisUtil.springCacheJoinKey(RedisKeyEnum.ONE_ONE_ONLINE_USER_ORDER_KEY.getKeyPrefix(), appKey);
        long now = System.currentTimeMillis();

        OnLineUserDto onLineUser = (OnLineUserDto) nemoRedisTemplate.opsForValue().get(userKey);
        if (onLineUser == null) {
            UserDto user = userService.getUser(userUuid);
            onLineUser = ObjectMapperUtil.map(user, OnLineUserDto.class);
            onLineUser.setFirstReportTime(now);
        }
        onLineUser.setLastReportTime(now);
        nemoRedisTemplate.opsForValue().set(userKey, onLineUser, 60, TimeUnit.SECONDS);
        nemoRedisTemplate.opsForZSet().add(orderKey, userUuid, now);
    }

    @Override
    public List<OnLineUserDto> getOnLineUser(String appKey, Integer pageNum, Integer pageSize) {
        List<OnLineUserDto> defaultOnLineUser = OneOneResourceUtil.getVirtually1V1Users();

        String key = RedisUtil.springCacheJoinKey(RedisKeyEnum.ONE_ONE_ONLINE_USER_KEY.getKeyPrefix(), appKey);
        String orderKey = RedisUtil.springCacheJoinKey(RedisKeyEnum.ONE_ONE_ONLINE_USER_ORDER_KEY.getKeyPrefix(), appKey);

        int totalCount = nemoRedisTemplate.opsForZSet().zCard(orderKey).intValue();
        int pageTotal = (totalCount + pageSize - 1) / pageSize;
        if (pageNum > pageTotal || pageNum < 0) {
            return defaultOnLineUser;
        }

        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = pageNum * pageSize - 1;
        Set<Object> userUuids = nemoRedisTemplate.opsForZSet().range(orderKey, startIndex, endIndex).stream().filter(o -> !o.equals(Context.get().getUserUuid())).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userUuids)) {
            return defaultOnLineUser;
        }

        List<String> userKeys = userUuids.stream().map(o -> RedisUtil.springCacheJoinKey(key, (String) o)).collect(Collectors.toList());
        List<OnLineUserDto> onLineUsers = nemoRedisTemplate.opsForValue().multiGet(userKeys)
                .stream()
                .map(o -> (OnLineUserDto) o)
                .sorted(Comparator.comparingLong(OnLineUserDto::getFirstReportTime))
                .collect(Collectors.toList());
        defaultOnLineUser.addAll(onLineUsers);

        if(defaultOnLineUser.size() > pageSize) {
            return defaultOnLineUser.subList(0, pageSize);
        }
        return defaultOnLineUser;
    }

    @Override
    public String getUserState(String appKey, String mobile) {
        UserDto user = userService.getUserByMobile(mobile);
        if(OneOneResourceUtil.virtually1V1UserUuids.contains(user.getUserUuid())) {
            return  "online";
        }
        String orderKey = RedisUtil.springCacheJoinKey(RedisKeyEnum.ONE_ONE_ONLINE_USER_ORDER_KEY.getKeyPrefix(), appKey);
        return null != nemoRedisTemplate.opsForZSet().score(orderKey, user.getUserUuid()) ? "online" : "offline";
    }

    @Override
    public UserDto getUserInfo(String appKey, String userUuid) {
        return userService.getUser(userUuid);
    }

    @Override
    public UserDto getUserInfo(String appKey, String userUuid, String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            return userService.getUser(userUuid);
        } else {
            return userService.getUserByUidAndDeviceId(userUuid, deviceId);
        }
    }

    @Override
    public void saveRtcRecord(String appKey, RtcRoomNotifyParam param) {
        if (param == null || StringUtils.isEmpty(param.getChannelId())) {
            log.info("rtc room illegal.");
            return;
        }
        String tabKey = RedisUtil.springCacheJoinKey(RTC_RECORD_KEY.getKeyPrefix(), appKey);
        RtcRoomInfoDto rtcRoomInfoDto = ObjectMapperUtil.map(param, RtcRoomInfoDto.class);
        rtcRoomInfoDto.setAppKey(appKey);
        nemoRedisTemplate.opsForHash().put(tabKey, param.getChannelId(), rtcRoomInfoDto);
    }

    @Override
    public void saveRtcUserRecord(String appKey, RtcRoomUserNotifyParam param) {
        if (param == null || StringUtils.isEmpty(param.getChannelId()) || null == param.getUid()) {
            log.info("rtc room user illegal.");
            return;
        }
        RtcRoomUserInfoDto rtcRoomUserInfoDto = ObjectMapperUtil.map(param, RtcRoomUserInfoDto.class);
        rtcRoomUserInfoDto.setAppKey(appKey);
        buildUserInfo(rtcRoomUserInfoDto);

        String rtcUserTableKey = RedisUtil.springCacheJoinKey(ONE_ONE_CHAT_RTC_USER_RECORD_KEY.getKeyPrefix(), appKey, rtcRoomUserInfoDto.getChannelId());
        nemoRedisTemplate.opsForHash().put(rtcUserTableKey, param.getUid(), rtcRoomUserInfoDto);
        nemoRedisTemplate.expire(rtcUserTableKey, Duration.ofDays(30));
    }

    @Override
    public List<RtcRoomUserInfoDto> getRtcRoomUsersByChannelId(String appKey, Long channelId) {
        if (channelId == null) {
            return Collections.emptyList();
        }
        String rtcUserTableKey = RedisUtil.springCacheJoinKey(ONE_ONE_CHAT_RTC_USER_RECORD_KEY.getKeyPrefix(), appKey, channelId);

        Map<Object, Object> entries = nemoRedisTemplate.opsForHash().entries(rtcUserTableKey);
        if (entries.isEmpty()) {
            return Collections.emptyList();
        }
        return entries.values().stream().map(o -> (RtcRoomUserInfoDto) o).collect(Collectors.toList());
    }

    @Override
    public RtcRoomInfoDto getRtcRoomInfoDtoByChannelId(String appKey, Long channelId) {
        if (channelId == null) {
            return null;
        }
        String rtcTableKey = RedisUtil.springCacheJoinKey(RTC_RECORD_KEY.getKeyPrefix(), appKey);
        return (RtcRoomInfoDto) nemoRedisTemplate.opsForHash().get(rtcTableKey, channelId);
    }

    @Override
    @Transactional
    public void userReward(UserRewardDto userRewardDto) {
        String appKey = Context.get().getAppKey();
        if (userRewardDto == null) {
            return;
        }
        Gift gift = giftMapperWrapper.selectByPrimaryKey(userRewardDto.getGiftId());
        if (gift == null) {
            throw new BsException(ErrorCode.GIFT_NOT_EXIST);
        }
        UserDto targetUser = userService.getUser(userRewardDto.getTarget());
        UserDto user = userService.getUser(userRewardDto.getUserUuid());

        UserReward userReward = ObjectMapperUtil.map(userRewardDto, UserReward.class);
        userReward.setCloudCoin(gift.getCloudCoin());

        userRewardMapperWrapper.insertSelective(userReward);

        // TODO
        // 被打赏者账户加礼物对应的云币
        // 打赏者账号减礼物对应的云币

        //发送打赏消息
        socialChatMessageService.notifyRewardMessage(userReward, targetUser, user);

    }

    @Override
    public List<UserDto> initOneOneVirtuallyUser() {
        List<OnLineUserDto> users = OneOneResourceUtil.getVirtually1V1Users();
        List<UserDto> result = new ArrayList<>(users.size());
        for (OnLineUserDto userDto : users) {
            User user = userMapper.selectByUserUuid(userDto.getUserUuid());
            if (user == null) {
                CreateUserParam createUserParam = new CreateUserParam();
                createUserParam.setMobile(userDto.getMobile());
                createUserParam.setIcon(userDto.getIcon());
                createUserParam.setUserName(userDto.getUserName());
                createUserParam.setUserUuid(userDto.getUserUuid());
                String token = UUIDUtil.getRandomString(16);
                createUserParam.setUserToken(token);
                createUserParam.setImToken(token);
                createUserParam.setIsAddFriend(true);
                createUserParam.setIsCreateImAccId(true);
                createUserParam.setDeviceId(UUIDUtil.getUUID());
                result.add(userService.createUser(createUserParam));
            } else {
                result.add(UserDto.build(user));
            }
        }
        return result;
    }

    private void buildUserInfo(RtcRoomUserInfoDto rtcRoomUserInfoDto) {
        Long uid = rtcRoomUserInfoDto.getUid();
        UserDto userDto = userService.getUserByRtcUid(uid);
        if (userDto != null) {
            rtcRoomUserInfoDto.setUserUuid(userDto.getUserUuid());
            if (!StringUtils.isEmpty(userDto.getIcon())) {
                rtcRoomUserInfoDto.setIcon(userDto.getIcon());
            }
            if (!StringUtils.isEmpty(userDto.getUserName())) {
                rtcRoomUserInfoDto.setUserName(userDto.getUserName());
            }
        }
    }
}
