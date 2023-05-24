package com.netease.nemo.socialchat.dto.message;

import com.netease.nemo.dto.UserDto;
import com.netease.nemo.model.po.User;
import com.netease.nemo.model.po.UserReward;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class RewardMessage {
    private String senderUserUuid;
    private Long sendTime;
    private String rewarderUserUuid;
    private String rewarderUserName;
    private Long giftId;
    private Integer giftCount;
    private String targetUserUuid;
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
        if(!StringUtils.isEmpty(targetUser.getUserName())) {
            rewardMessage.setTargetUserName(targetUser.getUserName());
        }
        rewardMessage.setRewarderUserUuid(user.getUserUuid());
        if(!StringUtils.isEmpty(user.getUserName())) {
            rewardMessage.setRewarderUserName(user.getUserName());
        }
        return rewardMessage;
    }
}
