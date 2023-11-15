package com.netease.nemo.openApi.dto.sud.event;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * 忽然游戏开始事件
 *
 * @Author：CH
 * @Date：2023/8/23 10:22 AM
 */
@Data
@Builder
public class GameStartReqData {
    private String uid;
    @SerializedName("room_id")
    private String roomId;
    @SerializedName("report_game_info_extras")
    private String reportGameInfoExtras;
    @SerializedName("report_game_info_key")
    private String reportGameInfoKey;
}
