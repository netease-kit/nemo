package com.netease.nemo.socialchat.service.impl;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.context.Context;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.mapper.GiftMapper;
import com.netease.nemo.mapper.UserRewardMapper;
import com.netease.nemo.model.po.Gift;
import com.netease.nemo.model.po.UserReward;
import com.netease.nemo.service.UserService;
import com.netease.nemo.socialchat.dto.OnLineUserDto;
import com.netease.nemo.socialchat.dto.UserRewardDto;
import com.netease.nemo.socialchat.dto.rtc.RtcRoomInfoDto;
import com.netease.nemo.socialchat.dto.rtc.RtcRoomUserInfoDto;
import com.netease.nemo.socialchat.parameter.rtcNotify.RtcRoomNotifyParam;
import com.netease.nemo.socialchat.parameter.rtcNotify.RtcRoomUserNotifyParam;
import com.netease.nemo.socialchat.service.OneToOneChatService;
import com.netease.nemo.socialchat.service.SocialChatMessageService;
import com.netease.nemo.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.netease.nemo.enums.RedisKeyEnum.ONE_ONE_CHAT_RTC_RECORD_KEY;
import static com.netease.nemo.enums.RedisKeyEnum.ONE_ONE_CHAT_RTC_USER_RECORD_KEY;

@Service
@Slf4j
public class OneToOneChatServiceImpl implements OneToOneChatService {

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private SocialChatMessageService socialChatMessageService;

    @Resource
    private UserRewardMapper userRewardMapper;

    @Resource
    private GiftMapper giftMapper;

    @Override
    public void reporter(String userUuid, String deviceId) {
        String key = RedisKeyEnum.ONE_ONE_ONLINE_USER_KEY.getKeyPrefix();
        String orderKey = RedisKeyEnum.ONE_ONE_ONLINE_USER_ORDER_KEY.getKeyPrefix();
        String filedKey = key + ":" + userUuid;
        long now = System.currentTimeMillis();

        OnLineUserDto onLineUser = (OnLineUserDto) nemoRedisTemplate.opsForValue().get(filedKey);
        if (onLineUser == null) {
            UserDto user = userService.getUser(userUuid);
            onLineUser = ObjectMapperUtil.map(user, OnLineUserDto.class);
            onLineUser.setFirstReportTime(now);
        }
        onLineUser.setLastReportTime(now);
        nemoRedisTemplate.opsForValue().set(filedKey, onLineUser, 60, TimeUnit.SECONDS);
        nemoRedisTemplate.opsForZSet().add(orderKey, userUuid, now);
    }

    @Override
    public List<OnLineUserDto> getOnLineUser(Integer pageNum, Integer pageSize) {
        String key = RedisKeyEnum.ONE_ONE_ONLINE_USER_KEY.getKeyPrefix();
        String orderKey = RedisKeyEnum.ONE_ONE_ONLINE_USER_ORDER_KEY.getKeyPrefix();
        int totalCount = nemoRedisTemplate.opsForZSet().zCard(orderKey).intValue();
        int pageTotal = (totalCount + pageSize - 1) / pageSize;
        if (pageNum > pageTotal || pageNum < 0) {
            return Collections.emptyList();
        }
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = pageNum * pageSize - 1;
        Set<Object> userUuids = nemoRedisTemplate.opsForZSet().range(orderKey, startIndex, endIndex).stream().filter(o -> !o.equals(Context.get().getUserUuid())).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userUuids)) {
            return Collections.emptyList();
        }
        List<String> fieldKeys = userUuids.stream().map(o -> key + ":" + ((String) o)).collect(Collectors.toList());

        return nemoRedisTemplate.opsForValue().multiGet(fieldKeys)
                .stream()
                .map(o -> (OnLineUserDto) o)
                .sorted(Comparator.comparingLong(OnLineUserDto::getFirstReportTime))
                .collect(Collectors.toList());
    }

    @Override
    public String getUserState(String mobile) {
        UserDto user = userService.getUserByMobile(mobile);
        String orderKey = RedisKeyEnum.ONE_ONE_ONLINE_USER_ORDER_KEY.getKeyPrefix();
        return null != nemoRedisTemplate.opsForZSet().score(orderKey, user.getUserUuid()) ? "online" : "offline";
    }

    @Override
    public UserDto getUserInfo(String userUuid) {
        return userService.getUser(userUuid);
    }

    @Override
    public UserDto getUserInfo(String userUuid, String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            return userService.getUser(userUuid);
        } else {
            return userService.getUserByUidAndDeviceId(userUuid, deviceId);
        }
    }

    @Override
    public void saveRtcRecord(RtcRoomNotifyParam param) {
        if (param == null || StringUtils.isEmpty(param.getChannelId())) {
            log.info("rtc room illegal.");
            return;
        }
        nemoRedisTemplate.opsForHash().put(ONE_ONE_CHAT_RTC_RECORD_KEY.getKeyPrefix(), param.getChannelId(), ObjectMapperUtil.map(param, RtcRoomInfoDto.class));
    }

    @Override
    public void saveRtcUserRecord(RtcRoomUserNotifyParam param) {
        if (param == null || StringUtils.isEmpty(param.getChannelId()) || null == param.getUid()) {
            log.info("rtc room user illegal.");
            return;
        }
        RtcRoomUserInfoDto rtcRoomUserInfoDto = ObjectMapperUtil.map(param, RtcRoomUserInfoDto.class);
        buildUserInfo(rtcRoomUserInfoDto);

        String rtcUserTableKey = ONE_ONE_CHAT_RTC_USER_RECORD_KEY.getKeyPrefix() + rtcRoomUserInfoDto.getChannelId();
        nemoRedisTemplate.opsForHash().put(rtcUserTableKey, param.getUid(), rtcRoomUserInfoDto);
    }

    @Override
    public List<RtcRoomUserInfoDto> getRtcRoomUsersByChannelId(Long channelId) {
        if (channelId == null) {
            return Collections.emptyList();
        }
        String rtcUserTableKey = ONE_ONE_CHAT_RTC_USER_RECORD_KEY.getKeyPrefix() + channelId;

        Map<Object, Object> entries = nemoRedisTemplate.opsForHash().entries(rtcUserTableKey);
        if (entries.isEmpty()) {
            return Collections.emptyList();
        }
        return entries.values().stream().map(o -> (RtcRoomUserInfoDto) o).collect(Collectors.toList());
    }

    @Override
    public RtcRoomInfoDto getRtcRoomInfoDtoByChannelId(Long channelId) {
        if (channelId == null) {
            return null;
        }

        return (RtcRoomInfoDto) nemoRedisTemplate.opsForHash().get(ONE_ONE_CHAT_RTC_RECORD_KEY.getKeyPrefix(), channelId);
    }

    @Override
    public void userReward(UserRewardDto userRewardDto) {
        if (userRewardDto == null) {
            return;
        }
        Gift gift = giftMapper.selectByPrimaryKey(userRewardDto.getGiftId());
        if (gift == null) {
            throw new BsException(ErrorCode.GIFT_NOT_EXIST);
        }
        UserDto targetUser = userService.getUser(userRewardDto.getTarget());
        UserDto user = userService.getUser(userRewardDto.getUserUuid());

        UserReward userReward = ObjectMapperUtil.map(userRewardDto, UserReward.class);
        userReward.setCloudCoin(gift.getCloudCoin());

        userRewardMapper.insertSelective(userReward);

        // TODO
        // 被打赏者账户加礼物对应的云币
        // 打赏者账号减礼物对应的云币

        //发送打赏消息
        socialChatMessageService.notifyRewardMessage(userReward, targetUser, user);

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
