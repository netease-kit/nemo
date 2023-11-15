package com.netease.nemo.game.enums;

import lombok.Getter;

/**
 * @Author：CH
 * @Date：2023/9/7 2:35 PM
 */
@Getter
public enum GameReportTypeEnum {
    GAME_START("game_start"), GAME_SETTLE("game_settle");

    private final String reportType;

    GameReportTypeEnum(String reportType) {
        this.reportType = reportType;
    }


    public static GameReportTypeEnum fromCode(String reportType) {
        for (GameReportTypeEnum types : values()) {
            if (types.reportType.equals(reportType)) {
                return types;
            }
        }
        return null;
    }
}
