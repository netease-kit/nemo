package com.netease.nemo.entlive.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SeatModeEnum {
    FREE_STYLE(0, "自由模式，观众可自由上麦"),
    UNFREE_STYLE(1, "非自由模式：观众上麦时需要管理员同意"),
    ;

    private final Integer type;
    private final String desc;

    public static SeatModeEnum fromCode(Integer type) {
        for (SeatModeEnum types : values()) {
            if (types.type.equals(type)) {
                return types;
            }
        }
        return null;
    }
}
