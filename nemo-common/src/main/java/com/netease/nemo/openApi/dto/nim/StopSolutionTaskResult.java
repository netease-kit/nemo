package com.netease.nemo.openApi.dto.nim;

import lombok.Data;

@Data
public class StopSolutionTaskResult {
    private String taskId;
    private Integer status;
    private String channelName;
}
