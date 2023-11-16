package com.netease.nemo.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 忽然游戏战斗开始通知
 *
 * @Author：CH
 * @Date：2023/9/7 2:12 PM
 */
@Data
public class GameStartDto {
    @JsonProperty(value = "mg_id")
    public String mgId;
    @JsonProperty(value = "mg_id_str")
    public String mgIdStr;
    @JsonProperty(value = "room_id")
    public String roomId;
    @JsonProperty(value = "game_mode")
    public int gameMode;
    @JsonProperty(value = "game_round_id")
    public String gameRoundId;
    @JsonProperty(value = "battle_start_at")
    public int battleStartAt;
    @JsonProperty(value = "players")
    public List<Player> players;
    @JsonProperty(value = "report_game_info_extras")
    public String reportGameInfoExtras;
    @JsonProperty(value = "report_game_info_key")
    public String reportGameInfoKey;


    public static class Player {
        public String uid;
        @JsonProperty(value = "is_ai")
        public int isAi;
    }

}
