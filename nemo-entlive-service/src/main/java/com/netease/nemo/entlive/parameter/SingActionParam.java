package com.netease.nemo.entlive.parameter;

import lombok.Data;

/**
 * 演唱操作对象
 *
 * @Author：CH
 * @Date：2023/10/11 13:10 AM
 */
@Data
public class SingActionParam {

    /**
     * NeRoom房间编号
     */
    private String roomUuid;

    /**
     * 歌曲操作，0: 暂停  1：继续   2：结束
     */
    private Integer action;
}
