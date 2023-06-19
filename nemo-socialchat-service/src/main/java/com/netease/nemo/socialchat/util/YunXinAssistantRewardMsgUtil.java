package com.netease.nemo.socialchat.util;

import com.google.gson.JsonObject;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.model.po.Gift;
import com.netease.nemo.model.po.UserReward;

public class YunXinAssistantRewardMsgUtil {

    /**
     * 构建云信小秘书打赏消息 msg
     * @param userReward 打赏记录
     * @param gift 礼物信息
     * @param rewardUser 打赏者账账户
     * @return
     */
    public static JsonObject buildRewardMsg(UserReward userReward, Gift gift, UserDto rewardUser) {
        JsonObject jsonObject = new JsonObject();
        StringBuilder sb = new StringBuilder("恭喜！您刚刚收到 ");
        sb.append(rewardUser.getUserName()).append(" 赠送的礼物：").append(gift.getGiftName()).append("x").append(userReward.getGiftCount());
        sb.append("</br><a href=\"party://chat/p2pChat?user=").append(rewardUser.getUserUuid()).append("\">快去感谢TA吧～</a>");

        jsonObject.addProperty("msg", sb.toString());
        return jsonObject;
    }
}
