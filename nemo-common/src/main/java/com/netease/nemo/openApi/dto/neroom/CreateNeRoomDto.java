package com.netease.nemo.openApi.dto.neroom;

import com.google.gson.annotations.SerializedName;
import com.netease.nemo.util.ObjectMapperUtil;
import lombok.Data;

import java.util.Optional;

@Data
public class CreateNeRoomDto {
    private String roomArchiveId;

    /**
     * 该房间关联的资源信息
     */
    @SerializedName("room_resource_info")
    private RoomResourceInfo roomResourceInfo;

    public LiveConfig getLiveConfig() {
        return Optional.ofNullable(roomResourceInfo).map(RoomResourceInfo::getLiveConfig).orElse(null);
    }

    public Long getChatRoomId() {
        return Optional.ofNullable(roomResourceInfo).map(RoomResourceInfo::getChatRoomId).orElse(0L);
    }


    @Data
    public static class RoomResourceInfo{
        @SerializedName("chat_room_id")
        private Long chatRoomId;
        @SerializedName("live_config")
        private LiveConfig liveConfig;
    }

    @Data
    public static class LiveConfig {
        @SerializedName("push_url")
        private String pushUrl;

        @SerializedName("pull_hls_url")
        private String pullHlsUrl;

        @SerializedName("pull_rtmp_url")
        private String pullRtmpUrl;

        @SerializedName("pull_http_url")
        private String pullHttpUrl;

        @SerializedName("pull_rts_url")
        private String pullRtsUrl;
    }

    /**
     * 下划线序列化后使用驼峰式命名，兼容老版本
     */
    @Data
    public static class LiveConfigCamelCase {
        private String pushUrl;
        private String pullHlsUrl;
        private String pullRtmpUrl;
        private String pullHttpUrl;
        private String pullRtsUrl;

        public static LiveConfigCamelCase convert(LiveConfig liveConfig) {
            return ObjectMapperUtil.map(liveConfig, LiveConfigCamelCase.class);
        }
    }
}
