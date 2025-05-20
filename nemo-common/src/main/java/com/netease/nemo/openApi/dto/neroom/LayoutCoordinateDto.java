package com.netease.nemo.openApi.dto.neroom;

import lombok.Data;

import java.util.List;

@Data
public class LayoutCoordinateDto {


    private LiveCanvas layoutCanvas;

    private List<BaseCoordinate> layoutList;


    @Data
    public static class LiveCanvas {
        private Integer width;
        private Integer height;
        private Integer zOrder;
    }

    @Data
    public static class BaseCoordinate {
        private String streamType;
        private String userUuid;
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;
        private Integer adaption;
        private Boolean pushAudio;
        private Boolean pushVideo;
    }

}
