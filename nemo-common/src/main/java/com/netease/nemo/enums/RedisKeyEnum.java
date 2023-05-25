package com.netease.nemo.enums;

import lombok.Getter;

@Getter
public enum RedisKeyEnum {
    // 1V1娱乐社交
    ONE_ONE_ONLINE_USER_KEY("one_one_online_user_key","1v1娱乐社交在线用户信息key"),
    ONE_ONE_ONLINE_USER_ORDER_KEY("one_one_online_user_order_key","1v1娱乐社交在线用户List"),
    ONE_ONE_RTC_CLOSE_KEY("one_one_rtc_close_key","1v1音视频通话"),

    ONE_ONE_CHAT_RTC_RECORD_KEY("one_one_chat_rtc_record_","1v1娱乐社交RTC房间信息记录【hashMap】"),
    ONE_ONE_CHAT_RTC_USER_RECORD_KEY("one_one_chat_room_rtc_user_record_","1v1娱乐社交RTC成员信息记录【hashMap】"),
    ANTISPAM_VIOLATIONS_LIST_KEY("antispam_violations_list","安全通审核违规数据记录key"),
    ANTISPAM_VIOLATIONS_USER_LIST_KEY("antispam_violations_user_list","安全通审核违规数据记录key"),
    TAG_USER_LOCK("USER_LOCK", "用户锁");

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
