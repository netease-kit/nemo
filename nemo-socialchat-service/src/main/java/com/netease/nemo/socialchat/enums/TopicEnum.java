package com.netease.nemo.socialchat.enums;

import lombok.Getter;

@Getter
public enum TopicEnum {
    TRAVEL_AND_EXPLORATION(1, "旅游风景"),
    MUSIC_AND_ART(2, "音乐歌星"),
    GOURMET_FOOD_AND_COOKING(3, "美食料理"),
    SPORTS_AND_FITNESS(4, "健身锻炼"),
    MOVIES_AND_TV_SERIES(5, "电影大片"),
    PETS_AND_ANIMALS(6, "宠物动物"),
    TECHNOLOGY_AND_GAMES(7, "游戏陪玩"),
    FASHION_AND_BEAUTY(8, "时尚穿着"),
    LITERATURE_AND_READING(9, "小说漫画"),
    ;
    private final int type;
    private final String desc;

    TopicEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static TopicEnum fromCode(Integer action) {
        for (TopicEnum types : values()) {
            if (types.type == action) {
                return types;
            }
        }
        return null;
    }

}
