package com.netease.nemo.openApi.dto.sud.event;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * 房间清理事件
 *
 * @Author：CH
 * @Date：2023/8/23 10:55 AM
 */
@Data
@Builder
public class RoomClearReqData {
    @SerializedName("room_id")
    private String roomId;
}
