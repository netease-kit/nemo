package com.netease.nemo.game.dto;

import com.netease.nemo.util.gson.GsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 忽然游戏战斗结算通知
 *
 * @Author：CH
 * @Date：2023/9/7 2:12 PM
 */
@Data
public class GameSettleDto {
    @GsonProperty(value = "mg_id")
    public String mgId;
    @GsonProperty(value = "mg_id_str")
    public String mgIdStr;
    @GsonProperty(value = "room_id")
    public String roomId;
    @GsonProperty(value = "game_mode")
    public int gameMode;
    @GsonProperty(value = "game_round_id")
    public String gameRoundId;
    @GsonProperty(value = "battle_start_at")
    public int battleStartAt;

    @GsonProperty(value = "battle_end_at")
    public int battleEndAt;

    @GsonProperty(value = "battle_duration")
    public int battleDuration;
    @GsonProperty(value = "players")
    public List<Player> results;

    @GsonProperty(value = "results_url")
    public String resultsUrl;
    @GsonProperty(value = "report_game_info_extras")
    public String reportGameInfoExtras;
    @GsonProperty(value = "report_game_info_key")
    public String reportGameInfoKey;
    @GsonProperty(value = " extras")
    public String extras;


    public static class Player {
        public String uid;
        @GsonProperty(value = "is_ai")
        public int isAi;
    }

}
