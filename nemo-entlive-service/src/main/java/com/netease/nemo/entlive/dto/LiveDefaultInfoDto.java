package com.netease.nemo.entlive.dto;

import lombok.Data;

import java.util.List;

@Data
public class LiveDefaultInfoDto {
    private String topic;
    private String livePicture;

    private List<String> defaultPictures;

    public LiveDefaultInfoDto() {
    }

    public LiveDefaultInfoDto(String topic, String livePicture, List<String> defaultPictures) {
        this(topic, livePicture);
        this.defaultPictures = defaultPictures;
    }

    public LiveDefaultInfoDto(String topic, String livePicture) {
        this.topic = topic;
        this.livePicture = livePicture;
    }
}
