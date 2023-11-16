package com.netease.nemo.openApi.dto.sud.event;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 忽然游戏快速开始事件
 *
 * @Author：CH
 * @Date：2023/8/23 10:22 AM
 */
@Data
@Builder
public class QuickStartReqData {
    private String[] codes;
    @SerializedName("user_infos")
    private List<UserInfo> userInfos;
    private String uid;
    @SerializedName("room_id")
    private String roomId;
    private String language;
    private Rule rule;
    @SerializedName("report_game_info_extras")
    private String reportGameInfoExtras;
    @SerializedName("report_game_info_key")
    private String reportGameInfoKey;


    @Data
    public static class UserInfo  {
        /**
         * 用户ID，字段名：uid
         */
        @SerializedName("uid")
        private String uid;

        /**
         * 昵称，字段名：nick_name
         */
        @SerializedName("nick_name")
        private String nickName;

        /**
         * 头像地址（最优解为128*128），字段名：avatar_url
         */
        @SerializedName("avatar_url")
        private String avatarUrl;

        /**
         * 性别（男：male, 女：female），字段名：gender
         */
        @SerializedName("gender")
        private String gender;
    }

    @Data
    public static class Rule {

    }
}
