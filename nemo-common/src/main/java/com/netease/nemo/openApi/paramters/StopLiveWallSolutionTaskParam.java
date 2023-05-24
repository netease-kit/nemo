package com.netease.nemo.openApi.paramters;

import lombok.Data;

import java.util.List;

@Data
public class StopLiveWallSolutionTaskParam {
    private List<ChannelInfo> realTimeInfoList;

    @Data
    public static class ChannelInfo {
        private String channelName;
        private Integer status;
        public ChannelInfo(String channelName,Integer status) {
            this.channelName = channelName;
            this.status = status;

        }
    }
}