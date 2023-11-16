package com.netease.nemo.openApi.dto.sud;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Author：CH
 * @Date：2023/8/30 3:48 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MgInfo {
    @JsonProperty("mg_id")
    private String mgId;
    private Map<String, String> name;
    private Map<String, String> desc;
    private Map<String, String> thumbnail332x332;
    private Map<String, String> thumbnail192x192;
    private Map<String, String> thumbnail128x128;
    private Map<String, String> thumbnail80x80;
    @JsonProperty("big_loading_pic")
    private Map<String, String> bigLoadingPic;
    @JsonProperty("game_mode_list")
    private List<GameMode> gameModeList;

    @Data
    public static class GameMode {
        private int mode;
        private int[] count;
        private int[] team_count;
        private int[] team_member_count;
        private String rule;
    }
}
