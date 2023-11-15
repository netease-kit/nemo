package com.netease.nemo.entlive.dto;


import com.netease.nemo.entlive.parameter.SingParam;
import lombok.Data;

import java.util.Map;

/**
 * 演唱基础信息
 */
@Data
public class SingBaseInfoDto {
    /**
     * 主唱userUuid
     */
    private String userUuid;

    /**
     * 副唱userUuid22
     */
    private String assistantUuid;

    /**
     * 点歌编号
     */
    private Long orderId;

    /**
     * 房间uuid
     * **/
    private String roomUuid;

    /**
     * 状态：0: 暂停  1:播放  2:结束  3:歌曲准备完成  4:未开始演唱  5:邀请中  6:加入合唱
     * **/
    private Integer songStatus;

    /**
     * 合唱编号
     * **/
    private String chorusId;

    /**
     * 应用编号
     * **/
    private String appKey;

    /**
     * 演唱的扩展信息
     * **/
    private Map<String, Object> ext;

    public SingBaseInfoDto() {
    }

    public SingBaseInfoDto(String userUuid, String assistantUuid, Long orderId, String roomUuid,
                           Integer songStatus, String chorusId, Map<String, Object> ext) {
        this.userUuid = userUuid;
        this.assistantUuid = assistantUuid;
        this.orderId = orderId;
        this.roomUuid = roomUuid;
        this.songStatus = songStatus;
        this.chorusId = chorusId;
        this.ext = ext;
    }

    public static SingBaseInfoDto buildSoloSing(SingParam singParam, String roomUuid, Integer songState,
                                                Map<String, Object> ext) {
        return new SingBaseInfoDto(singParam.getUserUuid(), null,
                singParam.getOrderId(), roomUuid, songState, singParam.getChorusId(), ext);
    }
}
