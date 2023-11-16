package com.netease.nemo.entlive.dto;


import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class LiveRecordDto {

    /**
     * 直播记录编号
     */
    @SerializedName("liveRecordId")
    private Long id;

    /**
     * NeRoom房间编号
     */
    private String roomUuid;

    /**
     * NeRoom房间名称
     * **/
    private String roomName;

    /**
     * NeRoom唯一主键
     */
    private String roomArchiveId;

    /**
     * 主播账户编号
     */
    private String userUuid;

    /**
     * 直播主题
     */
    private String liveTopic;

    /**
     * 封面URL，如果传空则自动生成
     */
    private String cover;

    /**
     * 直播状态：1.有效，-1.无效
     */
    private Integer status;

    /**
     * 直播状态: -1直播结束, 0.未开始，1.直播中
     */
    private Integer live;

    /**
     * 直播类型 1.互动直播 2.语聊房，3. KTV
     */
    private Integer liveType;

    /**
     * 演唱模式 0:智能合唱 1:串行合唱 2:NTP实时合唱 3:独唱
     */
    private Integer singMode;

    /**
     * 直播配置
     */
    private transient String liveConfig;

}
