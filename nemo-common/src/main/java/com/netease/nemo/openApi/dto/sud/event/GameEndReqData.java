package com.netease.nemo.openApi.dto.sud.event;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * 忽然游戏结束事件
 *
 * @Author：CH
 * @Date：2023/8/23 10:22 AM
 */
@Data
@Builder
public class GameEndReqData {
    private String uid;
    @SerializedName("room_id")
    private String roomId;
}
