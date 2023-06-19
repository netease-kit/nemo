package com.netease.nemo.entlive.parameter;

import lombok.Data;

@Data
public class OrderParam {
    /**
     * orderId
     */
    private Long orderId;

    /**
     * 直播间编号
     */
    private Long liveRecordId;
}
