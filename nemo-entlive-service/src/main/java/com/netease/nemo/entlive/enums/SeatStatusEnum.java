package com.netease.nemo.entlive.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SeatStatusEnum {
    INIT(0, "麦上无人，可上麦"),
    OCCUPY_SEAT(1, "麦被占，还未上麦"),
    ON_SEAT(2, "已上麦"),
    CLOSE_SEAT(-1, "麦位关闭，不能操作上麦"),
    ;

    private final int status;
    private final String desc;

    public static SeatStatusEnum fromCode(int status) {
        for (SeatStatusEnum seatStatus : values()) {
            if (seatStatus.status == status) {
                return seatStatus;
            }
        }
        return null;
    }

    public static boolean canApply(int status){
        return INIT.getStatus() == status;
    }
}
