package com.netease.nemo.socialchat.dto;

import lombok.Data;

@Data
public class UserRewardDto {
    /**
     * 礼物编号
     */
    private Long giftId;
    /**
     * 打赏礼物个数
     */
    private int giftCount;
    /**
     * 被打赏者账号
     */
    private String target;
    /**
     * 打赏者账号
     */
    private String userUuid;
}
