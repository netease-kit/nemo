package com.netease.nemo.socialchat.parameter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserRewardParam {
    /**
     * 礼物编号
     */
    @NotNull(message = "{giftId.notNull}")
    private Long giftId;
    /**
     * 打赏礼物个数
     */
    private int giftCount;
    /**
     * 被打赏者账号
     */
    @NotBlank(message = "{UserRewardParam.target.notNull}")
    private String target;
}
