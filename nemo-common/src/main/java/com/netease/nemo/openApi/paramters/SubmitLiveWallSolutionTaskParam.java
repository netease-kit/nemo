package com.netease.nemo.openApi.paramters;

import lombok.Data;

@Data
public class SubmitLiveWallSolutionTaskParam {
    private String channelName;
    private Long monitorUid;
    private Integer detectType;
    private Integer scFrequency;

    public SubmitLiveWallSolutionTaskParam(String channelName, Long monitorUid, Integer detectType, Integer scFrequency) {
        this.channelName =  channelName;
        this.monitorUid = monitorUid;
        this.detectType = detectType;
        if(scFrequency != null) {
            this.scFrequency = scFrequency;
        }
    }
}
