package com.netease.nemo.socialchat.service;

import com.google.gson.JsonObject;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.enums.EventTypeEnum;
import com.netease.nemo.mapper.GiftMapper;
import com.netease.nemo.model.po.User;
import com.netease.nemo.model.po.UserReward;
import com.netease.nemo.openApi.NimService;
import com.netease.nemo.openApi.dto.antispam.RtcAntispamDto;
import com.netease.nemo.openApi.enums.ImNotifyMsgTypeEnum;
import com.netease.nemo.openApi.enums.ImOpeEnum;
import com.netease.nemo.service.UserService;
import com.netease.nemo.socialchat.dto.message.EventDto;
import com.netease.nemo.socialchat.dto.message.AntisMessage;
import com.netease.nemo.socialchat.dto.message.RewardMessage;
import com.netease.nemo.socialchat.dto.message.UserUnBlockMessage;
import com.netease.nemo.socialchat.dto.rtc.RtcRoomInfoDto;
import com.netease.nemo.socialchat.dto.rtc.RtcRoomUserInfoDto;
import com.netease.nemo.socialchat.enums.RtcStatusEnum;
import com.netease.nemo.socialchat.util.YunXinAssistantRewardMsgUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 1v1娱乐社交消息服务
 */
@Service
@Slf4j
public class SocialChatMessageService {

    @Value("${business.systemAccid}")
    private String systemAccid;

    @Value("${business.yunxinAssistAccid}")
    private String yunxinAssistAccid;

    @Resource
    private OneToOneChatService oneToOneChatService;

    @Resource
    private GiftMapper giftMapper;

    @Resource
    private NimService nimService;

    @Resource
    private UserService userService;

    /**
     * 给用户发送音视频违规的消息事件
     *
     * @param rtcAntispamDto rtcAntispamDto
     */
    public void notifyAntispamMessage(RtcAntispamDto rtcAntispamDto) {
        AntisMessage antisMessage = AntisMessage.build(rtcAntispamDto);
        if (antisMessage == null) {
            log.info("antisMessage is null.");
            return;
        }
        Long channelId = rtcAntispamDto.getChannelId();
        RtcRoomInfoDto rtcRoomInfoDto = oneToOneChatService.getRtcRoomInfoDtoByChannelId(channelId);
        if (null == rtcRoomInfoDto || rtcRoomInfoDto.getStatus() == RtcStatusEnum.END.getStatus()) {
            log.info("rtcRoomInfoDto is null or status is End, channelId:{}", channelId);
            return;
        }
        List<RtcRoomUserInfoDto> roomUsers = oneToOneChatService.getRtcRoomUsersByChannelId(channelId);
        if (!CollectionUtils.isEmpty(roomUsers)) {
            List<String> toAccIds = roomUsers.stream().filter(o -> !StringUtils.isEmpty(o.getUserUuid())).map(RtcRoomUserInfoDto::getUserUuid).collect(Collectors.toList());
            nimService.sendBatchAttachMsg(systemAccid, toAccIds, new EventDto(antisMessage, EventTypeEnum.MESSAGE_CONTENT_MODERATION.getType()));
        }
    }

    /**
     * 发送解禁违规用户消息
     * @param rtcRoomUserDto rtc用户信息
     */
    public void notifyUserUnblockedMessage(RtcRoomUserInfoDto rtcRoomUserDto) {
        if (rtcRoomUserDto == null) {
            log.info("rtcRoomUserInfoDto is null.");
            return;
        }
        Long channelId = rtcRoomUserDto.getChannelId();
        RtcRoomInfoDto rtcRoomInfoDto = oneToOneChatService.getRtcRoomInfoDtoByChannelId(channelId);
        if (null == rtcRoomInfoDto || rtcRoomInfoDto.getStatus() == RtcStatusEnum.END.getStatus()) {
            log.info("rtcRoomInfoDto is null or status is End, channelId:{}", channelId);
            return;
        }
        UserUnBlockMessage userUnBlockMessage = new UserUnBlockMessage(rtcRoomUserDto.getChannelId(), rtcRoomUserDto.getChannelName(), rtcRoomUserDto.getUid());

        List<RtcRoomUserInfoDto> roomUsers = oneToOneChatService.getRtcRoomUsersByChannelId(channelId);
        if (!CollectionUtils.isEmpty(roomUsers)) {
            List<String> toAccIds = roomUsers.stream().filter(o -> !StringUtils.isEmpty(o.getUserUuid())).map(RtcRoomUserInfoDto::getUserUuid).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(toAccIds)) {
                nimService.sendBatchAttachMsg(systemAccid, toAccIds, new EventDto(userUnBlockMessage, EventTypeEnum.USER_UNBLOCK.getType()));
            }
        }
    }

    /**
     * 发送打赏消息
     * @param userReward 用户打赏记录
     * @param targetUser 被打赏者信息
     * @param user 打赏者用户信息
     */
    public void notifyRewardMessage(UserReward userReward, UserDto targetUser, UserDto user) {
        if (userReward == null) {
            return;
        }
        String target = userReward.getTarget();

        // 给用户发送打赏消息
        RewardMessage message = RewardMessage.build(user.getUserUuid(), userReward, user, targetUser);
        nimService.sendImCustomMsg(user.getUserUuid(), target, ImOpeEnum.SINGLE_CHAT_MESSAGE.getOpe(), new EventDto(message, EventTypeEnum.SOCIAL_CHAT_USER_REWARD.getType()));

        // 云信小秘书发送打赏消息
        JsonObject rewardMsg = YunXinAssistantRewardMsgUtil.buildRewardMsg(userReward, giftMapper.selectByPrimaryKey(userReward.getGiftId()), user);
        nimService.sendImCustomMsg(yunxinAssistAccid, target, ImOpeEnum.SINGLE_CHAT_MESSAGE.getOpe(), new EventDto(rewardMsg, EventTypeEnum.SOCIAL_CHAT_USER_REWARD_YUN_XIN_ASSIST.getType()));
    }

}
