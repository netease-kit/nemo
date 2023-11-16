package com.netease.nemo.openApi.dto.sud.event;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * 用户加入忽然游戏推送事件数据
 *
 * @Author：CH
 * @Date：2023/8/23 10:22 AM
 */
@Data
@Builder
public class UserInReqData {
    private String code;
    @SerializedName("room_id")
    private String roomId;
    private Integer mode;
    private String language;
    @SerializedName("seat_index")
    private Integer seatIndex;
    @SerializedName("is_seat_random")
    private Boolean isSeatRandom;
    @SerializedName("team_id")
    private String teamId;
    @SerializedName("is_ready")
    private Boolean isReady;
}
