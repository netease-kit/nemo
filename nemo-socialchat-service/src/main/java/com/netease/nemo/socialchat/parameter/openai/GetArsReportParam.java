package com.netease.nemo.socialchat.parameter.openai;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class GetArsReportParam {
    @NotBlank
    private String roomUuid;
    @NotNull
    private Long startTime;
    @NotNull
    private Long endTime;

    public GetArsReportParam() {}
    public GetArsReportParam(String roomUuid, Long startTime, Long endTime) {
        this.roomUuid = roomUuid;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
