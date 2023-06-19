package com.netease.nemo.entlive.dto;

import lombok.Data;

/**
 * 直播房间信息
 */
@Data
public class LiveIntroDto {

    /**
     * 主播信息
     */
    BasicUserDto anchor;

    /**
     * 直播信息
     */
    LiveDto live;

    public LiveIntroDto() {
    }

    public LiveIntroDto(BasicUserDto anchor, LiveDto live) {
        this.anchor = anchor;
        this.live = live;
    }
}
