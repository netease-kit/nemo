package com.netease.nemo.socialchat.dto.message;

import com.netease.nemo.dto.UserDto;
import com.netease.nemo.model.po.User;
import com.netease.nemo.model.po.UserReward;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class RewardMessage {
    /**
     * 消息发送者账号
     */
    private String senderUserUuid;
    /**
     * 消息发送时间
     */
    private Long sendTime;
    /**
     * 打赏者账号
     */
    private String rewarderUserUuid;
    /**
     * 打赏者昵称
     */
    private String rewarderUserName;
    /**
     * 礼物ID
     */
    private Long giftId;
    /**
     * 礼物数量
     */
    private Integer giftCount;
    /**
     * 被打赏者账号
     */
    private String targetUserUuid;
    /**
     * 被打赏者昵称
     */
    private String targetUserName;

    public static RewardMessage build(String senderUserUuid, UserReward userReward, UserDto user, UserDto targetUser) {
        RewardMessage rewardMessage = build(userReward, user, targetUser);
        rewardMessage.setSenderUserUuid(senderUserUuid);
        return rewardMessage;
    }

    public static RewardMessage build(UserReward userReward, UserDto user, UserDto targetUser) {
        RewardMessage rewardMessage = new RewardMessage();
        rewardMessage.setGiftCount(userReward.getGiftCount());
        rewardMessage.setGiftId(userReward.getGiftId());
        rewardMessage.setSendTime(System.currentTimeMillis());

        rewardMessage.setTargetUserUuid(targetUser.getUserUuid());
        if (!StringUtils.isEmpty(targetUser.getUserName())) {
            rewardMessage.setTargetUserName(targetUser.getUserName());
        }
        rewardMessage.setRewarderUserUuid(user.getUserUuid());
        if (!StringUtils.isEmpty(user.getUserName())) {
            rewardMessage.setRewarderUserName(user.getUserName());
        }
        return rewardMessage;
    }
}
