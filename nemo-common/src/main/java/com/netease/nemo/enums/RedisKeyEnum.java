package com.netease.nemo.enums;

import lombok.Getter;

@Getter
public enum RedisKeyEnum {
    // 1V1娱乐社交
    ONE_ONE_ONLINE_USER_KEY("one_one_online_user_key","1v1娱乐社交在线用户信息key"),
    ONE_ONE_ONLINE_USER_ORDER_KEY("one_one_online_user_order_key","1v1娱乐社交在线用户List"),
    ONE_ONE_RTC_CLOSE_KEY("one_one_rtc_close_key","1v1音视频通话"),

    RTC_RECORD_KEY("rtc_record_","1v1娱乐社交RTC房间信息记录【hashMap】"),
    ONE_ONE_CHAT_RTC_USER_RECORD_KEY("one_one_chat_room_rtc_user_record_","1v1娱乐社交RTC成员信息记录【hashMap】"),
    ANTISPAM_VIOLATIONS_LIST_KEY("antispam_violations_list","安全通审核违规数据记录key"),
    ANTISPAM_VIOLATIONS_USER_TABLE_KEY("antispam_violations_user_table","安全通审核违规数据记录key"),

    RTC_CLOUD_PLAYER_TASK_ID_KEY("rtc_cloud_player_task_id_","云端播放task key"),
    RTC_CLOUD_PLAYER_RTC_TASK_TABLE_KEY("rtc_cloud_player_rtc_task_table_key_","云端播放RTC 关联 taskId key"),

    TAG_USER_LOCK("USER_LOCK", "用户锁"),

    ENT_CREATE_LIVE_ROOM_LOCK_KEY("ent_create_live_room_lock_key_","创建直播房间lock key tag"),
    ENT_LIVE_ROOM_LOCK_KEY("ent_live_room_lock_key_","直播房间lock key tag"),
    NE_ROOM_MEMBER_TABLE_KEY("ent_ne_room_table_key","NeRoom成员表"),
    ENT_SONG_ORDER("ent_song_order_lock_key_","点歌台 lock key tag"),

    /**
     * 歌曲播放状态 lock key
     */
    ENT_MUSIC_PLAYER_LOCKER_KEY("ent_music_player_locker_key_","歌曲播放状态 lock key") ,

    /**
     * 直播房间播放歌曲信息key
     */
    ENT_MUSIC_PLAY_INFO_KEY("ent_music_play_info_", "直播房间播放歌曲信息key"),

    /**
     * 主播直播房间歌曲准备key  table  key：ent_music_play_ready_info_:{liveRecordId}:{orderId} filed: userUuid value: 歌曲状态
     */
    ENT_MUSIC_PLAY_READY_INFO_KEY("ent_music_play_ready_info_", "主播直播房间歌曲准备信息key");
    ;
    /** redis的key前缀 */
    private final String keyPrefix;
    /** 描述 */
    private final String desc;

    RedisKeyEnum(String keyPrefix, String desc) {
        this.keyPrefix = keyPrefix;
        this.desc = desc;
    }
}
