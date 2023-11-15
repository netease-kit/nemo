package com.netease.nemo.entlive.enums;

public enum OrderSongStatusEnum {
    CANCEL(-1, "删除点歌"),
    WAITING(0, "等待唱歌或者等待播放"),
    PLAYING(1, "唱歌中或者播放中"),
    PLAYED(2, "已唱或者已播放"),;

    /**
     * 状态编码
     */
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    OrderSongStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean effectiveOrderSong(Integer code) {
        if(null == code) {
            return false;
        }
        return CANCEL.getCode() != code;
    }

    public static boolean effectiveOrderSongForKtv(Integer code) {
        if(null == code) {
            return false;
        }
        return WAITING.getCode() == code || PLAYING.getCode() == code;
    }


    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
