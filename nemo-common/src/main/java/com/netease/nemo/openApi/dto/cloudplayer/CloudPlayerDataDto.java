package com.netease.nemo.openApi.dto.cloudplayer;

import com.google.gson.JsonObject;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.Data;

@Data
public class CloudPlayerDataDto {
    private Integer code;
    private Integer idleTimeout;
    private Integer playTs;
    private String streamUrl;
    private String taskId;
    private Long channelId;
    private Long timestamp;

    public static CloudPlayerDataDto build(JsonObject data) {
        return GsonUtil.fromJson(data, CloudPlayerDataDto.class);
    }
}
