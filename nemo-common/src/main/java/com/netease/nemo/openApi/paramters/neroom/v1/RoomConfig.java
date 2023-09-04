package com.netease.nemo.openApi.paramters.neroom.v1;

import lombok.Data;

@Data
public class RoomConfig {
    private RoomResourceConfig resource;

    public RoomConfig(RoomResourceConfig resourceConfig) {
        this.resource = resourceConfig;
    }
}
