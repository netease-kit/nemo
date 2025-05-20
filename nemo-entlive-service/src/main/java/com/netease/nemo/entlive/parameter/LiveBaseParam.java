package com.netease.nemo.entlive.parameter;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LiveBaseParam {
    @NotNull(message = "The live ID cannot be empty")
    private Long liveRecordId;
}
