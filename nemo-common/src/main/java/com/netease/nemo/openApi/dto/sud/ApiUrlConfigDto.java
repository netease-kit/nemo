package com.netease.nemo.openApi.dto.sud;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：CH
 * @Date：2023/8/22 4:00 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiUrlConfigDto {
    private ApiUrl api;
    @JsonProperty("bullet_api")
    private BulletApi bulletApi;
    @JsonProperty("match_api")
    private MatchApi matchApi;
    @JsonProperty("web3_api")
    private Web3Api web3Api;

    @Data
    public static class ApiUrl {
        @JsonProperty("get_mg_list")
        private String getMgList;
        @JsonProperty("get_mg_info")
        private String getMgInfo;
        @JsonProperty("mg_list")
        private String mgList;
        @JsonProperty("mg_info")
        private String mgInfo;
        @JsonProperty("get_game_report_info")
        private String getGameReportInfo;
        @JsonProperty("get_game_report_info_page")
        private String getGameReportInfoPage;
        @JsonProperty("query_game_report_info")
        private String queryGameReportInfo;
        @JsonProperty("report_game_round_bill")
        private String reportGameRoundBill;
        @JsonProperty("push_event")
        private String pushEvent;
        @JsonProperty("auth_app_list")
        private String authAppList;
        @JsonProperty("authRoomList")
        private String auth_room_list;
        @JsonProperty("create_order")
        private String createOrder;
        @JsonProperty("query_order")
        private String queryOrder;
        @JsonProperty("query_match_base")
        private String queryMatchBase;
        @JsonProperty("query_user_settle")
        private String queryUserSettle;
        @JsonProperty("query_match_round_ids")
        private String queryMatchRoundIds;
        @JsonProperty("get_player_results")
        private String getPlayerResults;
    }

    @Data
    public static class BulletApi {
        private String connect;
        private String operate;
        @JsonProperty("change_game")
        private String changeGame;
        private String disconnect;
        @JsonProperty("refresh_connection")
        private String refreshConnection;
        @JsonProperty("operate_game")
        private String operateGame;
        private String init;
        private String command;
        private String refresh;
    }

    @Data
    public static class MatchApi {
        @JsonProperty("create_match")
        private String createMatch;
        @JsonProperty("cancel_match")
        private String cancelMatch;
        @JsonProperty("query_game_config")
        private String queryGameConfig;
        @JsonProperty("query_user_matching")
        private String queryUserMatching;
    }

    @Data
    public static class Web3Api {
        @JsonProperty("get_details")
        private String getDetails;
        @JsonProperty("refresh_details")
        private String refreshDetails;
    }
}
