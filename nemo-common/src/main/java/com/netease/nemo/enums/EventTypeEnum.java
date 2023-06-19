package com.netease.nemo.enums;

import lombok.Getter;

@Getter
public enum EventTypeEnum {

    /**
     * 实时音视频安全通审核事件
     */
    MESSAGE_CONTENT_MODERATION(400, "实时音视频安全通审核事件"),

    /**
     * 云端录制安全通审核事件
     */
    CLOUD_RECORDING_CONTENT_MODERATION(401, "云端录制安全通审核事件"),

    /**
     *  1v1娱乐娱乐社交打赏事件
     */
    SOCIAL_CHAT_USER_REWARD(1004, "1v1娱乐娱乐社交打赏事件"),

    /**
     * 1V1娱乐娱乐社交云信小秘书发送打赏消息事件
     */
    SOCIAL_CHAT_USER_REWARD_YUN_XIN_ASSIST(1007, "1V1娱乐娱乐社交云信小秘书发送打赏消息事件"),

    /**
     * 娱乐直播间打赏事件
     */
    ENT_USER_REWARD(1005, "娱乐直播间打赏事件"),

    /**
     * 点歌事件
     */
    ENT_USER_ORDER_SONG(1008, "用户点歌"),
    /**
     * 取消点歌事件
     */
    ENT_USER_CANCEL_ORDER_SONG(1009, "取消点歌"),
    /**
     * 切歌事件
     */
    ENT_USER_SWITCH_SONG(1010, "切歌"),

    /**
     * 点歌置顶
     */
    ENT_USER_SONG_SET_TOP(1011, "置顶"),

    /**
     * 点歌列表变化事件
     */
    ENT_ORDER_SONG_LIST_CHANGE(1012, "点歌列表变化"),

    /**
     * 歌曲开始播放事件
     */
    ENT_MUSIC_PLAY(1013,"歌曲开始播放"),

    /**
     * 歌曲暂停播放事件
     */
    ENT_MUSIC_PAUSE(1014,"歌曲暂停播放"),

    /**
     * 歌曲准备ok事件
     */
    ENT_MUSIC_READY(1015,"歌曲准备ok"),

    /**
     * 歌曲恢复播放事件
     */
    ENT_MUSIC_RESUME_PLAY(1016,"歌曲恢复播放"),

    /**
     * 实时音视频安全通审核违规账号解禁事件
     */
    USER_UNBLOCK(3000, "实时音视频安全通审核违规账号解禁"),
    ;


    /**
     * 状态编码
     */
    private final int type;
    /**
     * 描述
     */
    private final String desc;

    EventTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
