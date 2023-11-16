package com.netease.nemo.entlive.parameter;

import lombok.Data;

/**
 * 放弃演唱对象
 *
 * @Author：CH
 * @Date：2023/10/11 13:03 AM
 */
@Data
public class AbandonSingParam {
    /**
     * NeRoom房间编号
     */
    private String roomUuid;

    /**
     * orderId
     */
    private Long orderId;
}
