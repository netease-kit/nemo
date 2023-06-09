package com.netease.nemo.entlive.dto.message;


import com.netease.nemo.entlive.dto.BasicUserDto;
import com.netease.nemo.entlive.dto.SeatUserRewardInfoDto;
import lombok.Data;

import java.util.List;

@Data
public class RewardContentMessage {

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 消息发送者用户编号
     */
    private String senderUserUuid;

    /**
     * 消息发送时间
     */
    private Long sendTime;

    /**
     * 打赏者用户编号
     */
    private String userUuid;

    /**
     * 打赏者昵称
     */
    private String userName;

    /**
     * 礼物编号
     */
    private Long giftId;

    /**
     * 礼物数
     */
    private Integer giftCount;

    /**
     * 麦位观众打赏信息
     */
    private List<SeatUserRewardInfoDto> seatUserReward;

    /**
     * 被打赏者列表
     */
    private List<BasicUserDto> targets;

}
