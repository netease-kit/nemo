package com.netease.nemo.entlive.enums;

import lombok.Getter;

@Getter
public enum LiveTypeEnum {
    INTERACTION_LIVE(1, "互动直播"),
    CHAT(2, "语聊房"),
    KTV(3, "KTV房间"),
    INTERACTION_LIVE_CROSS_CHANNEL(4, "跨频道转发-互动直播"),
    LISTEN_TOGETHER(5,"一起听"),;

    /**
     * 类型
     */
    private final int type;
    /**
     * 描述
     */
    private final String desc;


    LiveTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static boolean checkType(Integer type) {
        if(null == type) {
            return false;
        }
        for (LiveTypeEnum value : LiveTypeEnum.values()) {
            if (value.type == type) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPkLive(Integer type) {
        if(null == type) {
            return false;
        }
        return INTERACTION_LIVE.getType() == type || INTERACTION_LIVE_CROSS_CHANNEL.getType() == type;
    }
}
