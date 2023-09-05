package com.netease.nemo.openApi.paramters.neroom.v1;

import lombok.Data;

import java.util.Map;

@Data
public class PutRoomParam {
    private String userUuid;
    private String roomName;
    private Long configId;
    private RoomConfig config;
    private Map<String, Map<String, Object>> properties;
    private Integer liveSeconds;
    // 房间扩展信息
    private Map<String, Object> ext;


    public static PutRoomParam build(String roomName, Map<String, Map<String, Object>> properties, Long configId, RoomResourceConfig resourceConfig, Integer liveSeconds){
        PutRoomParam putRoomParam = new PutRoomParam();
        putRoomParam.setRoomName(roomName);
        putRoomParam.setConfigId(configId);
        RoomConfig roomConfig = new RoomConfig(resourceConfig);
        putRoomParam.setConfig(roomConfig);
        putRoomParam.setLiveSeconds(liveSeconds);
        putRoomParam.setProperties(properties);
        return putRoomParam;
    }
}
