package com.netease.nemo.openApi.dto.sud.event;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * 用户退出忽然游戏推送事件数据
 * @Author：CH
 * @Date：2023/8/23 10:22 AM
 */
@Data
@Builder
public class UserOutReqData {
    private String uid;
    @SerializedName("is_cancel_ready")
    private boolean isCancelReady;
}
