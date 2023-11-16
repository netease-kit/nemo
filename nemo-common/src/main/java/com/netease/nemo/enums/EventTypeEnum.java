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
     * KTV点歌台协议 1030——1037
     */
    /**
     * 邀请取消
     */
    CANCEL_CHORUS(1029, "邀请取消"),
    /**
     * 邀请合唱
     */
    INVITE_CHORUS(1030, "邀请合唱"),
    /**
     * 加入合唱
     */
    JOIN_CHORUS(1031, "加入合唱"),
    /**
     * 合唱准备完成
     */
    PREPARE_CHORUS(1032, "合唱准备完成-歌曲资源：歌词、原伴唱等下载OK"),
    /**
     * 开始演唱
     */
    START_SING(1033, "开始演唱"),
    /**
     * 暂停演唱
     */
    PAUSE_SING(1034, "暂停演唱"),
    /**
     * 继续演唱
     */
    CONTINUE_SING(1035, "继续演唱"),
    /**
     * 结束演唱
     */
    END_SING(1036, "结束演唱"),
    /**
     * 放弃演唱歌曲
     */
    ABANDON_SING(1037, "放弃演唱歌曲"),
    /**
     * 演唱下一首
     */
    PLAY_NEXT_SONG(1038, "演唱下一首"),

    /**
     * 游戏事件 1100——1104
     */

    /**
     * 游戏准备/加入游戏
     */
    GAME_READY(1100,"加入游戏/游戏准备"),

    /**
     * 退出游戏/取消准备
     */
    GAME_LEAVE(1101,"退出游戏/取消准备"),

    /**
     * 开始游戏
     */
    GAME_START(1102,"开始游戏"),

    /**
     * 游戏结束
     */
    GAME_END(1103,"游戏结束"),


    /**
     * 创建游戏|选择游戏
     */
    GAME_CREATE(1104,"游戏创建|选择游戏"),




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
