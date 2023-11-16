package com.netease.nemo.entlive.parameter;

import lombok.Data;

import java.util.Map;

/**
 * 演唱对象
 *
 * @Author：CH
 * @Date：2023/10/11 13:09 AM
 */
@Data
public class SingParam {
    /**
     * NeRoom房间编号
     */
    private String roomUuid;
    /**
     * 主唱userUuid
     */
    private String userUuid;

    /**
     * 点歌编号
     */
    private Long orderId;

    /**
     * 合唱编号
     */
    private String chorusId;

    /**
     * 演唱的扩展信息
     **/
    private Map<String, Object> ext;
}
