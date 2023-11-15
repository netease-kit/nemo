package com.netease.nemo.openApi.dto.sud.event;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

/**
 * 用户踢人
 *
 * @Author：CH
 * @Date：2023/8/23 10:55 AM
 */
@Data
@Builder
public class UserKickReqData {
    @SerializedName("kicked_uid")
    private String kickedUid;
}
