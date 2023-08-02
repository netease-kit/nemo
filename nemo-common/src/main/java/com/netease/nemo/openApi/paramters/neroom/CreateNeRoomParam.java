package com.netease.nemo.openApi.paramters.neroom;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CreateNeRoomParam {
    private Long templateId;
    private String roomUuid;
    private String roomName;
    private String password;
    private RoomConfig roomConfig;
    private ExternalLiveConfig externalLiveConfig;
    private RoomSeatConfig roomSeatConfig;


    @Data
    @NoArgsConstructor
    public static class RoomConfig {
        private ResourceConfig resource;

        public RoomConfig(ResourceConfig resource) {
            this.resource = resource;
        }
    }

    @Data
    public static class ResourceConfig {
        private Boolean whiteboard;
        private Boolean chatroom;
        private Boolean live;
        private Boolean rtc;
        private Boolean record;
        private Boolean sip;
        private Boolean seat;

        public static ResourceConfig buildEntVoiceRoom() {
            return new ResourceConfig(false, true, true, true, false, false, true);
        }

        public ResourceConfig(Boolean whiteboard, Boolean chatroom, Boolean live, Boolean rtc, Boolean record, Boolean sip, Boolean seat) {
            this.whiteboard = whiteboard;
            this.chatroom = chatroom;
            this.live = live;
            this.rtc = rtc;
            this.record = record;
            this.sip = sip;
            this.seat = seat;
        }
    }

    @Data
    public static class ExternalLiveConfig {
        private String pushUrl;
        private String pullHlsUrl;
        private String pullRtmpUrl;
        private String pullHttpUrl;
        private String pullRtsUrl;
    }

    @Data
    public static class RoomSeatConfig {
        /**
         * 麦序个数
         */
        private Integer seatCount;
        /**
         * 申请上麦时使用的模式：null或0（不需要管理员同意）、1（需要管理员同意）
         */
        private Integer applyMode;
        /**
         * 抱麦时使用的模式：null或0（不需要被邀请人同意）、1（需要被邀请人同意）
         */
        private Integer inviteMode;
    }

}
