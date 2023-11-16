package com.netease.nemo.openApi.dto.sud.event;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * 用户准备或者取消准备忽然游戏事件数据
 * @Author：CH
 * @Date：2023/8/23 10:22 AM
 */
@Data
@Builder
public class UserReadyReqData {
    private String uid;
    @SerializedName("is_ready")
    private boolean isReady;
}
