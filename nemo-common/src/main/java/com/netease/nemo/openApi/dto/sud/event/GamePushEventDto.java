package com.netease.nemo.openApi.dto.sud.event;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * 忽然游戏推送事件
 * @Author：CH
 * @Date：2023/8/23 9:42 AM
 */
@Data
@Builder
public class GamePushEventDto {
    private String event;
    @SerializedName("mg_id")
    private String mgId;
    @SerializedName("app_id")
    private String appId;
    private String timestamp;
    private Object data;
}
