package com.netease.nemo.entlive.parameter;

import lombok.Data;

@Data
public class CreateLiveParam {

    /**
     * 直播类型 1.互动直播 2.语聊房，3. KTV
     */
    private Integer liveType;

    /**
     * 直播主题
     */
    private String liveTopic;

    /**
     * 直播背景
     */
    private String cover;

    /**
     * 麦位的数量，seatCount>0则初始化麦位，最多20个麦位，传超过20的值会报错
     */
    private Integer seatCount;

    /**
     * 模麦位模式，0：自由模式，1：管理员控制模式，默认自由模式
     **/
    private Integer seatMode;

    /**
     * 申请上麦时使用的模式：null或0（不需要管理员同意）、1（需要管理员同意）
     */
    private Integer seatApplyMode;

    /**
     * 抱麦时使用的模式：null或0（不需要被邀请人同意）、1（需要被邀请人同意）
     */
    private Integer seatInviteMode;

    /**
     * 房间名称，不填默认生成
     */
    private String roomName;

    /**
     * 房间场景
     * COMMUNICATION（0）：通话场景。
     * LIVE_BROADCASTING（1）：直播场景。
     */
    private Integer roomProfile;

    /**
     * 房间时长
     */
    private Integer liveSeconds;

    /**
     * 扩展字段
     */
    private String ext;

    /**
     * NeRoom 模板ID
     */
    private Long configId;

}
