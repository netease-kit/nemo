package com.netease.nemo.entlive.parameter;


import lombok.Data;

import java.util.List;

@Data
public class LiveRewardParam {

    /**
     * 房间编号
     */
    private Long liveRecordId;

    /**
     * 礼物编号
     */
    private Long giftId;

    /**
     * 打赏礼物数
     */
    private Integer giftCount;

    /**
     * 打赏者用户Uuid
     */
    private String userUuid;

    /**
     * 被打赏者用户列表
     */
    private List<String> targets;
}
