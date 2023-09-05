package com.netease.nemo.entlive.enums;

import lombok.Getter;

@Getter
public enum MusicChannelEnum {
    NET_EASE_MUSIC(1, "网易云音乐版权"),
    MI_GU_MUSIC(2, "咪咕音乐版权"),
    HIFIVE_MUSIC(3, "hifive音乐版权渠道"),
    ;

    /**
     * channel
     */
    private final int code;
    /**
     * serviceName
     */
    private final String desc;

    MusicChannelEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MusicChannelEnum fromCode(Integer code){
        if(null == code) {
            return null;
        }
        for (MusicChannelEnum e : MusicChannelEnum.values()) {
            if (code == e.getCode()) {
                return e;
            }
        }
        return null;
    }
}
