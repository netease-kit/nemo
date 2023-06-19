package com.netease.nemo.entlive.dto;

import lombok.Data;

@Data
public class LiveDefaultInfoDto {
    private String topic;
    private String livePicture;

    public LiveDefaultInfoDto() {
    }

    public LiveDefaultInfoDto(String topic, String livePicture) {
        this.topic = topic;
        this.livePicture = livePicture;
    }
}
