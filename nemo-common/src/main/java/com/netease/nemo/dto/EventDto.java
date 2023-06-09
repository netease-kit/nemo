package com.netease.nemo.dto;

import lombok.Data;

@Data
public class EventDto {
    private Object data;
    private Integer type;

    public EventDto(Object data, int type) {
        this.data = data;
        this.type = type;
    }
}
